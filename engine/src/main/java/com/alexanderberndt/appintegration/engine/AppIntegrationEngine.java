package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ComponentInfoJson;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.ref.SoftReference;
import java.util.*;

public abstract class AppIntegrationEngine<I extends ApplicationInstance, C extends GlobalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Cache for application-infos.json objects
    private final Map<String, SoftReference<ApplicationInfoJson>> applicationInfoCache = new HashMap<>();

    protected abstract AppIntegrationFactory<I, C> getFactory();

    protected abstract C createGlobalContext(@Nonnull final String applicationId, @Nonnull final Application application);


    /* Runtime methods */

    public ExternalResource getHtmlSnippet(I instance) throws IOException {
        return null;
        //return loadHtmlSnippet(instance.getApplicationId(), instance.getComponentId(), instance);
    }

    public ExternalResource getStaticResource(String relativePath) {
        return null;
    }

    public boolean isDynamicPath(String relativePath) {
        return false;
    }

    public List<String> getDynamicPaths() {
        throw new UnsupportedOperationException("method not implemented!");
    }

    /* Prefetch methods */

    public void prefetch(@Nonnull final List<I> instanceList) throws IOException {

        LOG.info("prefetch {} instances in total", instanceList.size());

//        // verify instance and get referenced objects from the factory
//        List<VerifiedInstance<I>> verifiedInstanceList = new ArrayList<>();
//        for (I instance : instanceList) {
//            verifiedInstanceList.add(VerifiedInstance.verify(instance, getFactory()));
//        }

        // group instances by application-id
        final Map<String, List<I>> instanceMap = new HashMap<>();
        for (I instance : instanceList) {
            final String applicationId = instance.getApplicationId();
            final List<I> existingList = instanceMap.get(applicationId);
            if (existingList != null) {
                existingList.add(instance);
            } else {
                final List<I> newList = new ArrayList<>();
                newList.add(instance);
                instanceMap.put(applicationId, newList);
            }
        }

        for (Map.Entry<String, List<I>> applicationEntry : instanceMap.entrySet()) {
            final String applicationId = applicationEntry.getKey();
            final List<I> applicationInstanceList = applicationEntry.getValue();
            LOG.info("prefetch {} instances for application {}", applicationInstanceList.size(), applicationId);

            final Application application = this.getFactory().getApplication(applicationId);
            if (application == null) {
                LOG.error("Application {} is undefined", applicationId);
                // ToDo: Log in integration log
                continue;
            }

            final C context = this.createGlobalContext(applicationId, application);

            // build processing pipeline
            final String pipelineName = application.getProcessingPipelineName();
            final ProcessingPipeline pipeline = createProcessingPipeline(context, pipelineName);
            if (pipeline == null) {
                context.addError(String.format("Cannot create pipeline %s for application %s", pipelineName, applicationId));
                continue;
            }

            // add global properties
            final Map<String, Object> globalProperties = application.getGlobalProperties();
            if (globalProperties != null) {
                ResourceLogger resourceLogger = context.getIntegrationLog().createResourceLogger(new ExternalResourceRef("global", ExternalResourceType.ANY));
                final TaskLogger taskLogger = resourceLogger.createTaskLogger("set-globals", "Set Global Properties Task");
                final TaskContext taskContext = context.createTaskContext(
                        taskLogger, Ranking.GLOBAL, "global", ExternalResourceType.ANY, Collections.emptyMap());
                for (final Map.Entry<String, Object> propEntry : globalProperties.entrySet()) {
                    taskContext.setValue(propEntry.getKey(), propEntry.getValue());
                }
            }

            // set global context read-only - values are only written to
            context.getProcessingParams().setReadOnly();

            // load application-properties.json
            final ApplicationInfoJson applicationInfo = loadApplicationInfoJson(application);

            // resolve url for all instances (some may resolve to the same url)
            final Set<ExternalResourceRef> resolvedSnippetsSet = new LinkedHashSet<>();
            for (I instance : applicationInstanceList) {
                final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, Objects.requireNonNull(getFactory()));
                final ExternalResourceRef snippetRef = resolveSnippetResource(verifiedInstance, applicationInfo);
                resolvedSnippetsSet.add(snippetRef);
            }


            // Load all resolved snippets
            // ToDo: Replace with ExternalResourceSet
            final Set<ExternalResourceRef> referencedResourcesSet = new LinkedHashSet<>();
            for (ExternalResourceRef snippetRef : resolvedSnippetsSet) {
                final ExternalResource snippet = pipeline.loadAndProcessResourceRef(snippetRef, this::createExternalResource);
                referencedResourcesSet.addAll(snippet.getReferencedResources());
            }


            // load all referenced resources (which may could load more)
            for (ExternalResourceRef resourceRef : referencedResourcesSet) {
                final ExternalResource resource = pipeline.loadAndProcessResourceRef(resourceRef, this::createExternalResource);
                referencedResourcesSet.addAll(resource.getReferencedResources());
            }


            // ToDo: Implement Cache Provider

            // ToDo: Error handling or logging





        }
    }


    /* Management methods ??? */



    /* Internal methods */

    protected ExternalResource createExternalResource(InputStream inputStream, ExternalResourceRef resourceRef, ResourceLoader loader) {
        return new ExternalResource(inputStream, loader, resourceRef, () -> getFactory().getAllTextParsers());
    }

    protected ApplicationInfoJson loadApplicationInfoJson(@Nonnull Application application) throws IOException {
        final ResourceLoader loader = requireResourceLoader(application);
        final String url = application.getApplicationInfoUrl();
        ExternalResource res = loader.load(new ExternalResourceRef(url, ExternalResourceType.APPLICATION_PROPERTIES), this::createExternalResource);
        return res.getContentAsParsedObject(ApplicationInfoJson.class);
    }


    protected ExternalResourceRef resolveSnippetResource(
            @Nonnull VerifiedInstance<I> instance,
            @Nonnull ApplicationInfoJson applicationInfo) {

        final ComponentInfoJson componentInfo = applicationInfo.getComponents().get(instance.getComponentId());
        if (componentInfo == null) {
            throw new AppIntegrationException(
                    String.format("Unknown component %s for application %s", instance.getComponentId(), instance.getApplicationId()));
        }

        final String baseUrl = instance.getApplication().getApplicationInfoUrl();
        final String relativeUrlTemplate = componentInfo.getUrl();
        final String relativeUrl = resolveStringWithContextVariables(instance, relativeUrlTemplate);
        return instance.getResourceLoader().resolveRelativeUrl(baseUrl, relativeUrl, ExternalResourceType.HTML_SNIPPET);
    }


    protected String resolveStringWithContextVariables(@Nonnull final VerifiedInstance<I> instance,
                                                       @Nonnull final String inputString) {
        // evaluate context
        final Map<String, String> contextMap = new HashMap<>();
        for (ContextProvider<I> contextProvider : requireContextProviders(instance)) {
            final Map<String, String> curCtxMap = contextProvider.getContext(instance.getInstance());
            if (curCtxMap != null) {
                contextMap.putAll(curCtxMap);
            }
        }
        final String resolvedString = StringSubstitutor.replace(inputString, contextMap);
        if (StringUtils.containsAny(resolvedString, '$', '{', '}')) {
            throw new AppIntegrationException(String.format(
                    "Could not fully resolve template \"%s\". It remained \"%s\".", inputString, resolvedString));
        } else {
            return resolvedString;
        }
    }


    protected ExternalResource loadHtmlSnippet(@Nonnull final VerifiedInstance<I> instance) throws IOException {
        throw new UnsupportedOperationException("method not implemented!");

    }


    protected ProcessingPipeline createProcessingPipeline(@Nonnull C context, @Nonnull String pipelineName) {
        final ProcessingPipelineFactory<C> pipelineFactory = getFactory().getProcessingPipelineFactory();
        return Objects.requireNonNull(pipelineFactory).createProcessingPipeline(context, pipelineName);
    }

    @Nonnull
    protected Application requireApplication(@Nonnull String applicationId) {
        final Application application = this.getFactory().getApplication(applicationId);
        if (application == null) {
            throw new AppIntegrationException(String.format("Application %s is not defined!", applicationId));
        }
        return application;
    }

    @Nonnull
    private ResourceLoader requireResourceLoader(Application application) {
        final ResourceLoader loader = this.getFactory().getResourceLoader(application.getResourceLoaderName());
        if (loader == null) {
            throw new AppIntegrationException(String.format("ResourceLoader %s is not defined!", application.getResourceLoaderName()));
        }
        return loader;
    }


    @Nonnull
    protected List<ContextProvider<I>> requireContextProviders(VerifiedInstance<I> instance) {
        final List<String> notFoundContextProviders = new ArrayList<>();
        final List<ContextProvider<I>> contextProviders = new ArrayList<>();
        for (final String providerName : instance.getApplication().getContextProviderNames()) {
            final ContextProvider<I> provider = getFactory().getContextProvider(providerName);
            if (provider != null) {
                contextProviders.add(provider);
            } else {
                notFoundContextProviders.add(providerName);
            }
        }
        if (notFoundContextProviders.isEmpty()) {
            return contextProviders;
        } else {
            throw new AppIntegrationException(String.format(
                    "The requested context providers %s for application %s are not available",
                    notFoundContextProviders, instance.getApplicationId()));
        }
    }


//    private String resolveUrl(String url, List<ContextProvider<I>> contextProviderList, I instance) {
//        Map<String, String> context = new HashMap<>();
//        for (ContextProvider<I> contextProvider : contextProviderList) {
//            context.putAll(contextProvider.getContext(instance));
//        }
//        return StringSubstitutor.replace(url, context);
//    }
//
//
//    @Nonnull
//    private ApplicationRecord getApplicationRecord(@Nonnull String applicationId) {
//        return Optional.ofNullable(applicationRecordMap.get(applicationId))
//                .orElseThrow(() -> new AppIntegrationException("Unknown application-id " + applicationId));
//    }
//
//
//    private class ApplicationRecord {
//
//        private final String applicationId;
//
//        private final Application application;
//
//        private ResourceLoader resourceLoader = null;
//
//        private List<ContextProvider<I>> contextProviderList = null;
//
//        public ApplicationRecord(String applicationId, Application application) {
//            this.applicationId = applicationId;
//            this.application = application;
//        }
//
//        public String getApplicationInfoUrl() {
//            return application.getApplicationInfoUrl();
//        }
//
//        public void clearResourceLoader(String resourceLoaderId) {
//            if (StringUtils.equals(resourceLoaderId, application.getResourceLoaderName())) {
//                this.resourceLoader = null;
//            }
//        }
//
//        public ResourceLoader getResourceLoader() {
//            if (this.resourceLoader == null) {
//                final String resourceLoaderId = application.getResourceLoaderName();
//                if (StringUtils.isBlank(resourceLoaderId)) {
//                    throw new AppIntegrationException("Application " + applicationId + " has no resource-loader defined");
//                }
//                this.resourceLoader = Optional.ofNullable(resourceLoaderMap.get(resourceLoaderId))
//                        .orElseThrow(() -> new AppIntegrationException("Unsupported resource-loader " + resourceLoaderId + " of application " + applicationId));
//            }
//            return this.resourceLoader;
//        }
//
//        public String resolveRelativeUrl(String relativeUrl) {
//            return null;
//            //return this.getResourceLoader().resolveRelativeUrl(application.getApplicationInfoUrl(), relativeUrl);
//        }
//
//        public void clearContextProvider(String contextProviderId) {
//            if ((application.getContextProviderNames() != null) && (application.getContextProviderNames().contains(contextProviderId))) {
//                this.contextProviderList = null;
//            }
//        }
//
//        public List<ContextProvider<I>> getContextProviders() {
//            if (this.contextProviderList == null) {
//                final List<String> usedContextProviderIdList = application.getContextProviderNames();
//                if ((usedContextProviderIdList == null) || usedContextProviderIdList.isEmpty()) {
//                    this.contextProviderList = Collections.emptyList();
//                } else {
//                    // verify, all context providers exists
//                    final String unsupportedContextProviders = usedContextProviderIdList.stream()
//                            .filter(id -> !contextProviderMap.containsKey(id))
//                            .collect(Collectors.joining(", "));
//
//                    if (StringUtils.isNotBlank(unsupportedContextProviders)) {
//                        throw new AppIntegrationException("Unsupported context providers " + unsupportedContextProviders + " of application " + applicationId);
//                    }
//
//                    this.contextProviderList = usedContextProviderIdList.stream().map(contextProviderMap::get).collect(Collectors.toList());
//                }
//            }
//            return this.contextProviderList;
//        }
//    }
}
