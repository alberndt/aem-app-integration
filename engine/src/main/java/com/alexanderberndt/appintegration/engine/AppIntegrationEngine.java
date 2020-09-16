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
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

public abstract class AppIntegrationEngine<I extends ApplicationInstance, C extends GlobalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Cache for instance to URI of html-snippet mapping (grouped by application id)
    private final Map<String, Map<I, URI>> instanceToSnippetUriMapCache = Collections.synchronizedMap(new HashMap<>());

    // Cache for application-infos.json objects
    private final Map<URI, ApplicationInfoJson> applicationInfoCache = Collections.synchronizedMap(new HashMap<>());

    @Nonnull
    protected abstract AppIntegrationFactory<I, C> getFactory();

    protected abstract <R> R callWithGlobalContext(String applicationId, Function<C, R> function);

    protected abstract <R> R callWithExternalResourceCache(String applicationId, Function<ExternalResourceCache, R> function);


    /* Runtime methods */

    public ExternalResource getHtmlSnippet(I instance) throws IOException {

        final String applicationId = instance.getApplicationId();
        final Application application = this.getFactory().getApplication(applicationId);
        if (application == null) {
            LOG.error("Application {} is undefined", applicationId);
            return null;
        }


        // ToDo: Integration-Log should differ by type (only save for pre-fetch)
        return callWithGlobalContext(applicationId, context -> getExternalResource(context, applicationId, application, instance));

//
//
//            Optional.ofNullable()..
//
//            orElse(null);
//
//            // resolve instance to url
//            URI uri = new URI("");
//            ExternalResourceRef resourceRef = new ExternalResourceRef(uri, ExternalResourceType.HTML_SNIPPET);
//
//            // try the cached version
//            if (this.
//
//                    isCachingEnabled() && (application.getFetchingMode() != FetchingMode.LIVE_LOAD_ONLY)) {
//
//                final ExternalResource resource = callWithExternalResourceCache(applicationId,
//                        cache -> cache.getCachedResource(resourceRef, this::createExternalResource));
//
//                if (resource != null) {
//                    return resource;
//                }
//            }
//
//            // try live-fetching
//            if (application.getFetchingMode() != FetchingMode.PREFETCH_ONLY) {
//                // get pipeline
//
//                // load
//
//                // get pipeline
//                final ProcessingPipelineFactory pipelineFactory = this.getFactory().getProcessingPipelineFactory();
//                final ProcessingPipeline pipeline = pipelineFactory.createProcessingPipeline(application.getProcessingPipelineName());
//
//                //
//                pipeline.declareTaskPropertiesAndDefaults();
//
//
//            }
//
//        }
//        return null;
//        //return loadHtmlSnippet(instance.getApplicationId(), instance.getComponentId(), instance);
    }

    protected ExternalResource getExternalResource(C context, String applicationId, Application application, I instance) {

        final ResourceLoader loader = requireInjectResourceLoader(context, application);
        final ProcessingPipeline pipeline = getFactory().createProcessingPipeline(context, application.getProcessingPipelineName());
        pipeline.initContextWithTaskDefaults(context);
        pipeline.initContextWithPipelineConfig(context);

        // inject resource

        // check, if the URI is cached
        final URI cachedSnippetUri = Optional.of(instanceToSnippetUriMapCache)
                .map(snippetUriCache -> snippetUriCache.get(applicationId))
                .map(innerSnippetUriCache -> innerSnippetUriCache.get(instance))
                .orElse(null);


        final URI snippetUri;
        if (cachedSnippetUri != null) {
            snippetUri = cachedSnippetUri;
        } else {
            // get application-info.json

            //final ApplicationInfoJson applicationInfoJson = getOrLoadApplicationInfoJson(application);
            final URI appInfoUri = getApplicationInfoUri(application, loader);

            final ApplicationInfoJson applicationInfoJson;
            final ApplicationInfoJson cachedAppInfo = applicationInfoCache.get(appInfoUri);
            if (cachedAppInfo != null) {
                applicationInfoJson = cachedAppInfo;
            } else {
                try {
                    // ToDo: load application-info.json via pipeline
                    final ExternalResourceRef appInfoResourceRef = new ExternalResourceRef(appInfoUri, ExternalResourceType.APPLICATION_PROPERTIES);
                    final ExternalResource loadedAppInfoResource = pipeline.loadAndProcessResourceRef(context, appInfoResourceRef, getFactory().getExternalResourceFactory());
                    applicationInfoJson = loadedAppInfoResource.getContentAsParsedObject(ApplicationInfoJson.class);
                    this.applicationInfoCache.put(appInfoUri, applicationInfoJson);
                } catch (IOException e) {
                    throw new AppIntegrationException("Cannot load application-info.json", e);
                }
            }

            // resolve snippet
            final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, this.getFactory());
            //snippetRef = resolveSnippetResource(verifiedInstance, applicationInfoJson);
            final String componentId = instance.getComponentId();
            final ComponentInfoJson componentInfo = applicationInfoJson.getComponents().get(componentId);
            if (componentInfo == null) {
                throw new AppIntegrationException(
                        String.format("Unknown component %s for application %s", instance.getComponentId(), instance.getApplicationId()));
            }

            final String relativeUrlTemplate = componentInfo.getUrl();
            final String relativeUrl = resolveStringWithContextVariables(verifiedInstance, relativeUrlTemplate);
            snippetUri = appInfoUri.resolve(relativeUrl).normalize();
        }

        final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
        final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef, getFactory().getExternalResourceFactory());

        return snippetRes;
    }

    @Nonnull
    private URI getApplicationInfoUri(Application application, ResourceLoader loader) {
        final URI appInfoUri;
        try {
            appInfoUri = loader.resolveBaseUri(application.getApplicationInfoUrl());
        } catch (URISyntaxException e) {
            throw new AppIntegrationException("Cannot resolve application-information.json url " + application.getApplicationInfoUrl(), e);
        }
        return appInfoUri;
    }


    @Nonnull
    private ResourceLoader requireInjectResourceLoader(@Nonnull C context, @Nonnull Application application) {
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = getFactory().getResourceLoader(resourceLoaderName);
        if (resourceLoader != null) {
            context.setResourceLoader(resourceLoader);
            return resourceLoader;
        } else {
            // ToDo: Nice logging
            throw new AppIntegrationException(String.format("ResourceLoader %s is not defined!", resourceLoaderName));
        }

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

    public void prefetch(@Nonnull final List<I> instanceList) {

        LOG.info("prefetch {} instances in total", instanceList.size());

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

            callWithGlobalContext(applicationId, context -> {
                prefetch(context, applicationId, applicationInstanceList);
                return true;
            });
        }
    }


    protected void prefetch(C context, String applicationId, List<I> applicationInstanceList) {

        final Application application = this.getFactory().getApplication(applicationId);
        if (application == null) {
            context.getIntegrationLog().addError("Application %s is undefined", applicationId);
            return;
        }

        // inject the resource-loader
        requireInjectResourceLoader(context, application);

        // build processing pipeline
        final ProcessingPipeline pipeline = setupProcessingPipeline(context, applicationId, application);

        // add global properties
        final Map<String, Object> globalProperties = application.getGlobalProperties();
        if (globalProperties != null) {
            ResourceLogger resourceLogger = context.getIntegrationLog().createResourceLogger(ExternalResourceRef.create("global", ExternalResourceType.ANY));
            final TaskLogger taskLogger = resourceLogger.createTaskLogger("Set Global Properties Task", "set-globals");
            final TaskContext taskContext = context.createTaskContext(
                    taskLogger, Ranking.GLOBAL, "global", ExternalResourceType.ANY, null);
            for (final Map.Entry<String, Object> propEntry : globalProperties.entrySet()) {
                taskContext.setValue(propEntry.getKey(), propEntry.getValue());
            }
        }

        // set global context read-only - values are only written to
        context.getProcessingParams().setReadOnly();

        // load application-properties.json
        final ApplicationInfoJson applicationInfo = null;//loadApplicationInfoJson(application);

        // resolve url for all instances (some may resolve to the same url)
        final Set<ExternalResourceRef> resolvedSnippetsSet = new LinkedHashSet<>();
        for (I instance : applicationInstanceList) {
            final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, getFactory());
            final ExternalResourceRef snippetRef = resolveSnippetResource(verifiedInstance, applicationInfo);
            resolvedSnippetsSet.add(snippetRef);
        }

        // ToDo: Setup Context

        // Load all resolved snippets
        // ToDo: Replace with ExternalResourceSet
        final Set<ExternalResourceRef> referencedResourcesSet = new LinkedHashSet<>();
        for (ExternalResourceRef snippetRef : resolvedSnippetsSet) {
            final ExternalResource snippet;
            try {
                snippet = pipeline.loadAndProcessResourceRef(context, snippetRef, getFactory().getExternalResourceFactory());
                referencedResourcesSet.addAll(snippet.getReferencedResources());
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }
        }


        // load all referenced resources (which may could load more)
        for (ExternalResourceRef resourceRef : referencedResourcesSet) {
            final ExternalResource resource;
            try {
                resource = pipeline.loadAndProcessResourceRef(context, resourceRef, getFactory().getExternalResourceFactory());
                referencedResourcesSet.addAll(resource.getReferencedResources());
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }

        }


        // ToDo: Implement Cache Provider

        // ToDo: Error handling or logging
    }

    @Nonnull
    private ProcessingPipeline setupProcessingPipeline(C context, String applicationId, Application application) {
        final String pipelineName = application.getProcessingPipelineName();
        final ProcessingPipeline pipeline;
        try {
            pipeline = getFactory().createProcessingPipeline(context, pipelineName);
            pipeline.initContextWithTaskDefaults(context);
            pipeline.initContextWithPipelineConfig(context);
        } catch (AppIntegrationException e) {
            context.getIntegrationLog().addError("Cannot create pipeline %s for application %s", pipelineName, applicationId);
            throw new AppIntegrationException(String.format("Cannot create pipeline %s for application %s", pipelineName, applicationId), e);
        }
        return pipeline;
    }


    /* Management methods ??? */



    /* Internal methods */

//    @Nonnull
//    protected ApplicationInfoJson getOrLoadApplicationInfoJson(@Nonnull Application application) {
//        final ApplicationInfoJson cachedAppInfo = applicationInfoCache.get(application.getApplicationInfoUrl());
//        if (cachedAppInfo != null) {
//            return cachedAppInfo;
//        } else {
//            return loadApplicationInfoJson(application);
//        }
//    }
//
//    @Nonnull
//    protected ApplicationInfoJson loadApplicationInfoJson(@Nonnull Application application) {
//        final ResourceLoader loader = requireInjectResourceLoader(context, application);
//        try {
//            final URI uri;
//            try {
//                uri = loader.resolveBaseUri(application.getApplicationInfoUrl());
//            } catch (URISyntaxException e) {
//                throw new AppIntegrationException("Cannot load application-info '" + application.getApplicationInfoUrl() + "'!", e);
//            }
//            final ExternalResource loadedRes = loader.load(new ExternalResourceRef(uri, ExternalResourceType.APPLICATION_PROPERTIES), getFactory().getExternalResourceFactory());
//            final ApplicationInfoJson appInfo = loadedRes.getContentAsParsedObject(ApplicationInfoJson.class);
//            this.applicationInfoCache.put(application.getApplicationInfoUrl(), appInfo);
//
//            return appInfo;
//
//        } catch (ResourceLoaderException | IOException e) {
//            throw new AppIntegrationException("Cannot load application-info.json", e);
//        }
//    }

    @Nonnull
    protected ExternalResourceRef resolveSnippetResource(
            @Nonnull VerifiedInstance<I> instance,
            @Nonnull ApplicationInfoJson applicationInfo) {

        final ComponentInfoJson componentInfo = applicationInfo.getComponents().get(instance.getComponentId());
        if (componentInfo == null) {
            throw new AppIntegrationException(
                    String.format("Unknown component %s for application %s", instance.getComponentId(), instance.getApplicationId()));
        }

        final String baseUrl = instance.getApplication().getApplicationInfoUrl();
        final URI baseUri;
        try {
            baseUri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new AppIntegrationException("Cannot create URI for " + baseUrl, e);
        }
        final String relativeUrlTemplate = componentInfo.getUrl();
        final String relativeUrl = resolveStringWithContextVariables(instance, relativeUrlTemplate);
//        try {
        // ToDo: Re-work URI handling
        return resolveRelativeUrl(baseUri, relativeUrl, ExternalResourceType.HTML_SNIPPET);
//        } catch (URISyntaxException e) {
//            throw new AppIntegrationException("cannot resolve snippet - no valid URI", e);
//        }
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

    @Nonnull
    protected Application requireApplication(@Nonnull String applicationId) {
        final Application application = this.getFactory().getApplication(applicationId);
        if (application == null) {
            throw new AppIntegrationException(String.format("Application %s is not defined!", applicationId));
        }
        return application;
    }

    @Nonnull
    public ExternalResourceRef resolveRelativeUrl(@Nonnull URI baseUri, @Nonnull String relativeUrl, @Nonnull ExternalResourceType expectedType) {
        final URI resolvedUri = baseUri.resolve(relativeUrl).normalize();
        return new ExternalResourceRef(resolvedUri, expectedType);
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
