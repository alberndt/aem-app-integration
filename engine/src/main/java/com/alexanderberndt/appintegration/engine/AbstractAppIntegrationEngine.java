package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourcesSet;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ComponentInfoJson;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.utils.HashMapWithTimeout;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class AbstractAppIntegrationEngine<I extends ApplicationInstance, C extends GlobalContext<I, C>> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Cache for instance to URI of html-snippet mapping (grouped by application id)
    private final Map<String, Map<I, URI>> instanceToSnippetUriMapCache = Collections.synchronizedMap(new HashMap<>());

    // Cache for application-infos.json objects
    private final Map<URI, ApplicationInfoJson> applicationInfoCache = Collections.synchronizedMap(new HashMapWithTimeout<>());

    /* Runtime methods */

    protected ExternalResource getHtmlSnippet(@Nonnull C context, @Nonnull I instance) {

        final ApplicationInfoJson applicationInfoJson = getApplicationInfo(context, false);
        final URI snippetUri = getSnippetUri(context, instance, applicationInfoJson, false);

        final ProcessingPipeline pipeline = context.getProcessingPipeline();
        final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
        final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
        return Objects.requireNonNull(snippetRes);
    }


    protected ExternalResource getStaticResource(@Nonnull C context, @Nonnull String relativePath) {

        final ProcessingPipeline pipeline = context.getProcessingPipeline();
        final URI resourceUri = context.getApplicationInfoUri().resolve(relativePath).normalize();
        final ExternalResourceRef snippetRef = new ExternalResourceRef(resourceUri, ExternalResourceType.ANY);
        final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);

        return Objects.requireNonNull(snippetRes);
    }

    protected boolean isDynamicPath(@Nonnull C context, String relativePath) {
        for (String dynamicPath : getDynamicPaths(context)) {
            if (dynamicPath.startsWith(relativePath)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    protected List<String> getDynamicPaths(@Nonnull C context) {
        final ApplicationInfoJson applicationInfoJson = getApplicationInfo(context, false);
        final List<String> dynamicPathsList = applicationInfoJson.getDynamicPaths();
        return (dynamicPathsList != null) ? dynamicPathsList : Collections.emptyList();
    }

    /* Prefetch methods */


    protected void prefetch(C context, List<I> applicationInstanceList) {

        LOG.info("prefetch {} instances for application {}", applicationInstanceList.size(), context.getApplicationId());

        final String curJobName = "prefetch_" + DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        if (!context.getExternalResourceCache().startLongRunningWrite(curJobName)) {
            context.getIntegrationLog().addWarning("Couldn't lock resource-cache. Abort prefetch-operation!");
            return;
        }

        // load application-properties.json
        final ApplicationInfoJson applicationInfo = getApplicationInfo(context, true);

        // resolve url for all instances (some may resolve to the same url)
        final Set<URI> resolvedSnippetsSet = new LinkedHashSet<>();
        for (final I instance : applicationInstanceList) {
            final URI snippetUri = getSnippetUri(context, instance, applicationInfo, true);
            resolvedSnippetsSet.add(snippetUri);
        }

        final ProcessingPipeline pipeline = context.getProcessingPipeline();
        final ExternalResourcesSet referencedResourcesSet = new ExternalResourcesSet();
        for (URI snippetUri : resolvedSnippetsSet) {
            final ExternalResourceRef snippetRef = new ExternalResourceRef(snippetUri, ExternalResourceType.HTML_SNIPPET);
            try {
                final ExternalResource snippetRes = pipeline.loadAndProcessResourceRef(context, snippetRef);
                referencedResourcesSet.addAll(snippetRes.getReferencedResources());
                context.getExternalResourceCache().continueLongRunningWrite();
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }
        }


        // load all referenced resources (which may could load more)
        while (referencedResourcesSet.hasMoreUnprocessed()) {
            final ExternalResourceRef resourceRef = referencedResourcesSet.nextUnprocessed();
            final ExternalResource resource;
            try {
                resource = pipeline.loadAndProcessResourceRef(context, resourceRef);
                referencedResourcesSet.addAll(resource.getReferencedResources());
                context.getExternalResourceCache().continueLongRunningWrite();
            } catch (AppIntegrationException e) {
                LOG.error("cannot load", e);
            }

        }

        context.getExternalResourceCache().commitLongRunningWrite();


    }

    /* Helper methods */

    protected void groupInstancesByApplicationId(@Nonnull List<I> instanceList, @Nonnull BiConsumer<String, List<I>> consumer) {

        final Map<String, List<I>> instanceMap = new HashMap<>();

        // group by
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

        // call consumer
        for (Map.Entry<String, List<I>> entry : instanceMap.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }



    /* Management methods ??? */



    /* Internal methods */

    @Nonnull
    private ApplicationInfoJson getApplicationInfo(@Nonnull C context, boolean forceReload) {

        final URI appInfoUri = context.getApplicationInfoUri();

        // try from cache
        if (!forceReload) {
            final ApplicationInfoJson cachedAppInfoJson = applicationInfoCache.get(appInfoUri);
            if (cachedAppInfoJson != null) {
                return cachedAppInfoJson;
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
                applicationInfoCache.put(appInfoUri, appInfoJson);
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

    private String resolveStringWithContextVariables(@Nonnull final C context, @Nonnull final I instance, @Nonnull final String inputString) {

        // evaluate context
        final Map<String, String> contextMap = new HashMap<>();
        for (final ContextProvider<I> contextProvider : context.getContextProviderList()) {
            final Map<String, String> curCtxMap = contextProvider.getContext(instance);
            if (curCtxMap != null) {
                curCtxMap.forEach((key, value) -> {
                    if (value != null) {
                        try {
                            contextMap.put(key, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                        } catch (UnsupportedEncodingException e) {
                            contextMap.put(key, value);
                        }
                    } else {
                        contextMap.put(key, value);
                    }
                });
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

}
