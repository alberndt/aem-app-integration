package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ComponentInfoJson;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AppIntegrationEngine<I extends ApplicationInstance> {

    private static final ObjectMapper objectMapper = new ObjectMapper();


//    // applications
//    private Map<String, ApplicationRecord> applicationRecordMap = new LinkedHashMap<>();

    // Cache for application-infos.json objects
    private final Map<String, SoftReference<ApplicationInfoJson>> applicationInfoCache = new HashMap<>();

//    // helper services
//    private Map<String, ResourceLoader> resourceLoaderMap = new LinkedHashMap<>();
//    private Map<String, ContextProvider<I>> contextProviderMap = new LinkedHashMap<>();
//
//    // services
//    private ApplicationInfoLoader applicationInfoLoader = new ApplicationInfoLoader();

    protected abstract AppIntegrationFactory<I> getFactory();


    /* Runtime methods */

    public ExternalResource getHtmlSnippet(I instance) throws IOException {
        return loadHtmlSnippet(instance.getApplicationId(), instance.getComponentId(), instance);
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

    public void prefetch(List<I> instanceList, boolean prefetchResources) throws IOException {
        throw new UnsupportedOperationException("method not implemented!");
    }


    /* Management methods ??? */



    /* Internal methods */

    protected ApplicationInfoJson loadApplicationInfoJson(@Nonnull Application application) throws IOException {
        final ResourceLoader loader = requireResourceLoader(application);

        final String url = application.getApplicationInfoUrl();
        ExternalResource res = loader.load(new ExternalResourceRef(url, ExternalResourceType.APPLICATION_PROPERTIES));

        try {
            // ToDo: Use Resource Converter
            return objectMapper.readerFor(ApplicationInfoJson.class).readValue(res.getContentAsReader());
        } catch (JsonProcessingException e) {
            throw new AppIntegrationException(String.format("Cannot parse %s due to: %s", url, e.getMessage()), e);
        }
    }


    protected ExternalResourceRef resolveSnippetResource(
            @Nonnull VerifiedAppInstance<I> instance,
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


    protected String resolveStringWithContextVariables(@Nonnull final VerifiedAppInstance<I> instance, @Nonnull final String inputString) {
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


    protected ExternalResource loadHtmlSnippet(String applicationId, String componentId, I instance) throws IOException {
        throw new UnsupportedOperationException("method not implemented!");

//        // get global data
//
//        // get application-info
//
//        // get context
//
//        // get url
//
//        //
//
//        final ApplicationRecord app = getApplicationRecord(applicationId);
//        final ApplicationInfo appInfo = applicationInfoLoader.load(app.getResourceLoader(), app.getApplicationInfoUrl());
//
//        final ApplicationInfo.ComponentInfo componentInfo = appInfo.getComponents().get(componentId);
//        if (componentInfo == null) {
//            throw new AppIntegrationException("Cannot find info for component " + componentId + " of application " + applicationId);
//        }
//
//        final String resolvedRelativeComponentUrl = resolveUrl(componentInfo.getUrl(), app.getContextProviders(), instance);
//        final String resolvedAbsoluteComponentUrl = app.resolveRelativeUrl(resolvedRelativeComponentUrl);
//
//
//        String htmlSnippet = null;//app.getResourceLoader().load(resolvedAbsoluteComponentUrl, String.class);
//        if (StringUtils.isBlank(htmlSnippet)) {
//            throw new AppIntegrationException("Cannot load html-snippet " + resolvedAbsoluteComponentUrl);
//        }
//
//        return htmlSnippet;
    }


    protected ProcessingPipeline createProcessingPipeline(@Nonnull GlobalContext context, @Nonnull String applicationId) {
        final Application application = requireApplication(applicationId);
        return this.getFactory().createProcessingPipeline(context, application.getProcessingPipelineName());
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
    protected List<ContextProvider<I>> requireContextProviders(VerifiedAppInstance<I> instance) {
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
