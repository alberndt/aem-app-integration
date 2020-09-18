package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ComponentInfoJson;
import com.alexanderberndt.appintegration.engine.utils.VerifiedApplication;
import com.alexanderberndt.appintegration.engine.utils.VerifiedInstance;
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
    private final Map<URI, TimestampValue<ApplicationInfoJson>> applicationInfoCache = Collections.synchronizedMap(new HashMap<>());

    @Nonnull
    public abstract AppIntegrationFactory<I, C> getFactory();

    protected abstract <R> R callWithGlobalContext(@Nonnull VerifiedApplication application, @Nonnull Function<C, R> function);

    protected abstract <R> R callWithExternalResourceCache(String applicationId, Function<ExternalResourceCache, R> function);


    /* Runtime methods */

    public ExternalResource getHtmlSnippet(I instance) {

        final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, this.getFactory());
        // ToDo: Integration-Log should differ by type (only save for pre-fetch)

        return callWithGlobalContext(verifiedInstance.getVerifiedApplication(), context -> {
            final ProcessingPipeline pipeline = createPipelineAndInitContext(verifiedInstance.getApplication().getProcessingPipelineName(), context);
            // ToDo: Unify injecting context
            injectGlobalProperties(context, verifiedInstance.getApplication().getGlobalProperties());
            final URI snippetUri = getSnippetUri(context, verifiedInstance, pipeline);
            final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
            final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
            return Objects.requireNonNull(snippetRes);
        });
    }


    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {

        final VerifiedApplication verifiedApplication = VerifiedApplication.verify(applicationId, getFactory());

        return callWithGlobalContext(verifiedApplication, context -> {

            final ProcessingPipeline pipeline = createPipelineAndInitContext(verifiedApplication.getApplication().getProcessingPipelineName(), context);

            final URI baseURI;
            final String appInfoUrl = verifiedApplication.getApplication().getApplicationInfoUrl();
            try {
                baseURI = verifiedApplication.getResourceLoader().resolveBaseUri(appInfoUrl);
            } catch (URISyntaxException e) {
                throw new AppIntegrationException("Cannot resolve application-info.json url " + appInfoUrl, e);
            }

            final URI resourceUri = baseURI.resolve(relativePath).normalize();
            final ExternalResourceRef snippetRef = new ExternalResourceRef(resourceUri, ExternalResourceType.ANY);
            final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
            return Objects.requireNonNull(snippetRes);

        });
    }

    @SuppressWarnings("unused")
    public boolean isDynamicPath(String relativePath) {
        throw new UnsupportedOperationException("Not yet implemented!");
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
            final VerifiedApplication verifiedApplication = VerifiedApplication.verify(applicationId, getFactory());

            LOG.info("prefetch {} instances for application {}", applicationInstanceList.size(), applicationId);
            callWithGlobalContext(verifiedApplication, context -> {
                prefetch(context, verifiedApplication, applicationInstanceList);
                return true;
            });
        }
    }


    protected void prefetch(C context, VerifiedApplication verifiedApplication, List<I> applicationInstanceList) {

        final Application application = verifiedApplication.getApplication();

        // build processing pipeline
        final ProcessingPipeline pipeline = createPipelineAndInitContext(verifiedApplication.getApplication().getProcessingPipelineName(), context);

        // add global properties
        injectGlobalProperties(context, application.getGlobalProperties());

        // set global context read-only - values are only written to
        context.getProcessingParams().setReadOnly();


//        final URI snippetUri = getSnippetUri(context, verifiedInstance, pipeline);
//        final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
//        final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
//        return Objects.requireNonNull(snippetRes);

        // clear cache


        // load application-properties.json
        final URI appInfoUri = getApplicationInfoUri(verifiedApplication);
        final ApplicationInfoJson applicationInfo;
        synchronized (this) {
            this.applicationInfoCache.remove(appInfoUri);
            // ToDo: add forceReload parameter
            applicationInfo = getCachedOrLoadApplicationInfoJson(context, appInfoUri, pipeline);
        }

        // resolve url for all instances (some may resolve to the same url)
        final Set<URI> resolvedSnippetsSet = new LinkedHashSet<>();
        for (I instance : applicationInstanceList) {

            // ToDo: use Verified application
            final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, getFactory());

            // ToDo: Re-use application info
            resolvedSnippetsSet.add(getSnippetUri(context, verifiedInstance, pipeline));
        }


        // Load all resolved snippets
        // ToDo: Replace with ExternalResourceSet
        final Set<ExternalResourceRef> referencedResourcesSet = new LinkedHashSet<>();
        for (URI snippetUri : resolvedSnippetsSet) {
            final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
            try {
                final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
                referencedResourcesSet.addAll(snippetRes.getReferencedResources());
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }
        }


        // load all referenced resources (which may could load more)
        for (ExternalResourceRef resourceRef : referencedResourcesSet) {
            final ExternalResource resource;
            try {
                resource = pipeline.loadAndProcessResourceRef(context, resourceRef);
                referencedResourcesSet.addAll(resource.getReferencedResources());
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }

        }


        // ToDo: Implement Cache Provider

        // ToDo: Error handling or logging
    }



    /* Management methods ??? */



    /* Internal methods */

    @Nonnull
    protected ProcessingPipeline createPipelineAndInitContext(@Nonnull String processingPipelineName, @Nonnull C context) {
        try {
            final ProcessingPipeline pipeline = getFactory().createProcessingPipeline(context, processingPipelineName);
            pipeline.initContextWithTaskDefaults(context);
            pipeline.initContextWithPipelineConfig(context);
            // ToDo: Global properties
            // verifiedInstance.getApplication().getGlobalProperties()
            context.getProcessingParams().setReadOnly();
            return pipeline;
        } catch (AppIntegrationException e) {
            context.getIntegrationLog().addError("Cannot create pipeline %s", processingPipelineName);
            throw new AppIntegrationException(String.format("Cannot create pipeline %s", processingPipelineName), e);
        }
    }

    private void injectGlobalProperties(C context, Map<String, Object> globalProperties) {
        if (globalProperties != null) {
            ResourceLogger resourceLogger = context.getIntegrationLog().createResourceLogger(ExternalResourceRef.create("global", ExternalResourceType.ANY));
            final TaskLogger taskLogger = resourceLogger.createTaskLogger("Set Global Properties Task", "set-globals");
            final TaskContext taskContext = context.createTaskContext(
                    taskLogger, Ranking.GLOBAL, "global", ExternalResourceType.ANY, null);
            for (final Map.Entry<String, Object> propEntry : globalProperties.entrySet()) {
                taskContext.setValue(propEntry.getKey(), propEntry.getValue());
            }
        }
    }


    @Nonnull
    private URI getSnippetUri(@Nonnull C context, @Nonnull VerifiedInstance<I> verifiedInstance, ProcessingPipeline pipeline) {
        // check, if the URI is cached
        final URI cachedSnippetUri = Optional.of(instanceToSnippetUriMapCache)
                .map(snippetUriCache -> snippetUriCache.get(verifiedInstance.getApplicationId()))
                .map(innerSnippetUriCache -> innerSnippetUriCache.get(verifiedInstance.getInstance()))
                .orElse(null);


        final URI snippetUri;
        if (cachedSnippetUri != null) {
            snippetUri = cachedSnippetUri;
        } else {
            // get application-info.json
            final URI appInfoUri = getApplicationInfoUri(verifiedInstance.getVerifiedApplication());
            final ApplicationInfoJson applicationInfoJson = getCachedOrLoadApplicationInfoJson(context, appInfoUri, pipeline);

            // resolve snippet
            final String componentId = verifiedInstance.getComponentId();
            final ComponentInfoJson componentInfo = applicationInfoJson.getComponents().get(componentId);
            if (componentInfo == null) {
                throw new AppIntegrationException(
                        String.format("Unknown component %s for application %s", componentId, verifiedInstance.getApplicationId()));
            }

            final String relativeUrlTemplate = componentInfo.getUrl();
            final String relativeUrl = resolveStringWithContextVariables(verifiedInstance, relativeUrlTemplate);
            snippetUri = appInfoUri.resolve(relativeUrl).normalize();
        }
        return snippetUri;
    }

    @Nonnull
    private ApplicationInfoJson getCachedOrLoadApplicationInfoJson(@Nonnull C context, @Nonnull URI appInfoUri, @Nonnull ProcessingPipeline pipeline) {

        // try from cache
        final TimestampValue<ApplicationInfoJson> cachedAppInfoJson = applicationInfoCache.get(appInfoUri);
        if ((cachedAppInfoJson != null) && cachedAppInfoJson.isFresh()) {
            return cachedAppInfoJson.getValue();
        }

        // load
        synchronized (this) {
            try {
                final ExternalResourceRef appInfoResourceRef = new ExternalResourceRef(appInfoUri, ExternalResourceType.APPLICATION_PROPERTIES);
                final ExternalResource loadedAppInfoResource = pipeline.loadAndProcessResourceRef(context, appInfoResourceRef);
                final ApplicationInfoJson appInfoJson = loadedAppInfoResource.getContentAsParsedObject(ApplicationInfoJson.class);
                Objects.requireNonNull(appInfoJson);
                applicationInfoCache.put(appInfoUri, new TimestampValue<>(appInfoJson));
                return appInfoJson;
            } catch (IOException e) {
                throw new AppIntegrationException("Cannot load application-info.json", e);
            }
        }
    }

    @Nonnull
    private URI getApplicationInfoUri(@Nonnull VerifiedApplication verifiedApplication) {
        final ResourceLoader loader = verifiedApplication.getResourceLoader();
        final String appInfoUrl = verifiedApplication.getApplication().getApplicationInfoUrl();
        try {
            return loader.resolveBaseUri(appInfoUrl);
        } catch (URISyntaxException e) {
            throw new AppIntegrationException("Cannot resolve application-information.json url " + appInfoUrl, e);
        }
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

    @Nonnull
    protected List<ContextProvider<I>> requireContextProviders(@Nonnull VerifiedInstance<I> instance) {
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

    private static class TimestampValue<T> {

        private static final long CACHE_TIMEOUT_MILLIS = 1000L * 60L * 5L;

        private final T value;

        private final long timestamp;

        public TimestampValue(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public T getValue() {
            return value;
        }

        public boolean isFresh() {
            return (timestamp + CACHE_TIMEOUT_MILLIS) < System.currentTimeMillis();
        }
    }
}
