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

    public ExternalResource getHtmlSnippet(I instance) {

        final String applicationId = instance.getApplicationId();
        final Application application = this.getFactory().getApplication(applicationId);
        if (application == null) {
            LOG.error("Application {} is undefined", applicationId);
            return null;
        }


        // ToDo: Integration-Log should differ by type (only save for pre-fetch)
        return callWithGlobalContext(applicationId, context -> getExternalResource(context, applicationId, application, instance));
    }

    protected ExternalResource getExternalResource(C context, String applicationId, Application application, I instance) {

        final ResourceLoader loader = requireInjectResourceLoader(context, application);
        final ProcessingPipeline pipeline = getFactory().createProcessingPipeline(context, application.getProcessingPipelineName());
        pipeline.initContextWithTaskDefaults(context);
        pipeline.initContextWithPipelineConfig(context);
        context.getProcessingParams().setReadOnly();

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
            final URI appInfoUri = getApplicationInfoUri(application, loader);
            final ApplicationInfoJson applicationInfoJson = getCachedOrLoadApplicationInfoJson(context, appInfoUri, pipeline);

            // resolve snippet
            final VerifiedInstance<I> verifiedInstance = VerifiedInstance.verify(instance, this.getFactory());
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
        return Objects.requireNonNull(snippetRes);
    }

    private ApplicationInfoJson getCachedOrLoadApplicationInfoJson(C context, URI appInfoUri, ProcessingPipeline pipeline) {
        final ApplicationInfoJson applicationInfoJson;
        final ApplicationInfoJson cachedAppInfo = applicationInfoCache.get(appInfoUri);
        if (cachedAppInfo != null) {
            applicationInfoJson = cachedAppInfo;
        } else {
            try {
                final ExternalResourceRef appInfoResourceRef = new ExternalResourceRef(appInfoUri, ExternalResourceType.APPLICATION_PROPERTIES);
                final ExternalResource loadedAppInfoResource = pipeline.loadAndProcessResourceRef(context, appInfoResourceRef, getFactory().getExternalResourceFactory());
                applicationInfoJson = loadedAppInfoResource.getContentAsParsedObject(ApplicationInfoJson.class);
                this.applicationInfoCache.put(appInfoUri, applicationInfoJson);
            } catch (IOException e) {
                throw new AppIntegrationException("Cannot load application-info.json", e);
            }
        }
        return applicationInfoJson;
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
    private ResourceLoader requireResourceLoader(@Nonnull Application application) {
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = getFactory().getResourceLoader(resourceLoaderName);
        if (resourceLoader == null) {
            throw new AppIntegrationException(String.format("ResourceLoader %s is not defined!", resourceLoaderName));
        }
        return resourceLoader;
    }

    @Nonnull
    private ResourceLoader requireInjectResourceLoader(@Nonnull C context, @Nonnull Application application) {
        final ResourceLoader resourceLoader = requireResourceLoader(application);
        context.setResourceLoader(resourceLoader);
        return resourceLoader;
    }

    @SuppressWarnings("unused")
    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {
        final Application application = requireApplication(applicationId);
        final ResourceLoader loader = requireResourceLoader(application);

        final URI baseURI;
        final String appInfoUrl = application.getApplicationInfoUrl();
        try {
            baseURI = loader.resolveBaseUri(appInfoUrl);
        } catch (URISyntaxException e) {
            throw new AppIntegrationException("Cannot resolve application-info.json url " + appInfoUrl, e);
        }

        final URI resourceUri = baseURI.resolve(relativePath).normalize();
        final ExternalResourceRef resourceRef = new ExternalResourceRef(resourceUri, ExternalResourceType.ANY);

        final ExternalResource cachedRes =  this.callWithExternalResourceCache(applicationId,
                cache -> cache.getCachedResource(resourceRef, getFactory().getExternalResourceFactory())
        );

        if (cachedRes != null) {
            return cachedRes;
        } else {
            // load live  + refactor to make cache part of the pipeline
            throw new UnsupportedOperationException("Not yet implemented!");
        }
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
        final ResourceLoader loader = requireInjectResourceLoader(context, application);

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
        final URI appInfoUri = getApplicationInfoUri(application, loader);
        final ApplicationInfoJson applicationInfo = getCachedOrLoadApplicationInfoJson(context, appInfoUri, pipeline);

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
}
