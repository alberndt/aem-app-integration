package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ComponentInfoJson;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AppIntegrationEngine<I extends ApplicationInstance, C extends GlobalContext<I, C>> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Cache for instance to URI of html-snippet mapping (grouped by application id)
    private final Map<String, Map<I, URI>> instanceToSnippetUriMapCache = Collections.synchronizedMap(new HashMap<>());

    // Cache for application-infos.json objects
    private final Map<URI, TimestampValue<ApplicationInfoJson>> applicationInfoCache = Collections.synchronizedMap(new HashMap<>());

    // ToDo: Integration-Log should differ by type (only save for pre-fetch)
    protected abstract <R> R callRuntimeMethodWithContext(@Nonnull String applicationId, @Nonnull Function<C, R> function);

    protected abstract void callBackgroundMethodWithContext(@Nonnull String applicationId, @Nonnull Consumer<C> consumer);

    /* Runtime methods */

    public ExternalResource getHtmlSnippet(@Nonnull I instance) {

        return callRuntimeMethodWithContext(instance.getApplicationId(), context -> {

            final ApplicationInfoJson applicationInfoJson = getApplicationInfo(context, false);
            final URI snippetUri = getSnippetUri(context, instance, applicationInfoJson, false);

            final ProcessingPipeline pipeline = context.getProcessingPipeline();
            final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
            final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
            return Objects.requireNonNull(snippetRes);
        });
    }


    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {

        return callRuntimeMethodWithContext(applicationId, context -> {

            final ProcessingPipeline pipeline = context.getProcessingPipeline();
            final URI resourceUri = context.getApplicationInfoUri().resolve(relativePath).normalize();
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

            LOG.info("prefetch {} instances for application {}", applicationInstanceList.size(), applicationId);
            callBackgroundMethodWithContext(applicationId, context -> prefetch(context, applicationInstanceList));
        }
    }


    protected void prefetch(C context, List<I> applicationInstanceList) {

        // build processing pipeline
        final ProcessingPipeline pipeline = context.getProcessingPipeline();

        // load application-properties.json
        final ApplicationInfoJson applicationInfo = getApplicationInfo(context, true);

        // resolve url for all instances (some may resolve to the same url)
        final Set<URI> resolvedSnippetsSet = new LinkedHashSet<>();
        for (final I instance : applicationInstanceList) {
            final URI snippetUri = getSnippetUri(context, instance, applicationInfo, true);
            resolvedSnippetsSet.add(snippetUri);
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
    private ApplicationInfoJson getApplicationInfo(@Nonnull C context, boolean forceReload) {

        final URI appInfoUri = context.getApplicationInfoUri();

        // try from cache
        if (!forceReload) {
            final TimestampValue<ApplicationInfoJson> cachedAppInfoJson = applicationInfoCache.get(appInfoUri);
            if ((cachedAppInfoJson != null) && cachedAppInfoJson.isFresh()) {
                return cachedAppInfoJson.getValue();
            }
        }

        // load
        synchronized (this) {

            if (forceReload) {
                this.applicationInfoCache.remove(appInfoUri);
            }
            try {
                final ExternalResourceRef appInfoResourceRef = new ExternalResourceRef(appInfoUri, ExternalResourceType.APPLICATION_PROPERTIES);
                final ProcessingPipeline pipeline = context.getProcessingPipeline();
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


    private URI getSnippetUri(@Nonnull C context, @Nonnull I instance, @Nonnull ApplicationInfoJson applicationInfoJson, boolean forceReload) {

        if (!forceReload) {
            final URI cachedSnippetUri = Optional.of(instanceToSnippetUriMapCache)
                    .map(snippetUriCache -> snippetUriCache.get(instance.getApplicationId()))
                    .map(innerSnippetUriCache -> innerSnippetUriCache.get(instance))
                    .orElse(null);
            if (cachedSnippetUri != null) {
                return cachedSnippetUri;
            }
        }

        final ComponentInfoJson componentInfo = applicationInfoJson.getComponents().get(instance.getComponentId());
        if (componentInfo == null) {
            throw new AppIntegrationException(
                    String.format("Unknown component %s for application %s", instance.getComponentId(), instance.getApplicationId()));
        }

        final String snippetUrl = resolveStringWithContextVariables(context, instance, componentInfo.getUrl());
        final URI appInfoUri = context.getApplicationInfoUri();
        return appInfoUri.resolve(snippetUrl).normalize();
    }

    private String resolveStringWithContextVariables(@Nonnull final C context,
                                                     @Nonnull final I instance,
                                                     @Nonnull final String inputString) {
        // evaluate context
        final Map<String, String> contextMap = new HashMap<>();
        for (final ContextProvider<I> contextProvider : context.getContextProviderList()) {
            final Map<String, String> curCtxMap = contextProvider.getContext(instance);
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
