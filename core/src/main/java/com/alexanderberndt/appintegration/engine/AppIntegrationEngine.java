package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.*;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.tasks.processors.info.ApplicationInfoLoader;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AppIntegrationEngine<I extends ApplicationInstance> {

    // applications
    private Map<String, ApplicationRecord> applicationRecordMap = new LinkedHashMap<>();

    // helper services
    private Map<String, ResourceLoader> resourceLoaderMap = new LinkedHashMap<>();
    private Map<String, ContextProvider<I>> contextProviderMap = new LinkedHashMap<>();

    // services
    private ApplicationInfoLoader applicationInfoLoader = new ApplicationInfoLoader();


    public void registerApplication(@Nonnull String id, @Nonnull Application application) {
        applicationRecordMap.put(id, new ApplicationRecord(id, application));
    }

    public void unregisterApplication(@Nonnull String id) {
        applicationRecordMap.remove(id);
    }

    protected void prefetch(List<I> instanceList) throws IOException {

    }

    public String getHtmlSnippet(I instance) throws IOException {
        return loadHtmlSnippet(instance.getApplicationId(), instance.getComponentId(), instance);
    }

    public InputStream getStaticResource(String path) {
        // ToDo: Support caching headers
        return null;
    }

    public List<String> getDynamicPaths() {
        return null;
    }



    public String loadHtmlSnippet(String applicationId, String componentId, I instance) throws IOException {
        final ApplicationRecord app = getApplicationRecord(applicationId);
        final ApplicationInfo appInfo = applicationInfoLoader.load(app.getResourceLoader(), app.getApplicationInfoUrl());

        final ApplicationInfo.ComponentInfo componentInfo = appInfo.getComponents().get(componentId);
        if (componentInfo == null) {
            throw new AppIntegrationException("Cannot find info for component " + componentId + " of application " + applicationId);
        }

        final String resolvedRelativeComponentUrl = resolveUrl(componentInfo.getUrl(), app.getContextProviders(), instance);
        final String resolvedAbsoluteComponentUrl = app.resolveRelativeUrl(resolvedRelativeComponentUrl);


        String htmlSnippet = null;//app.getResourceLoader().load(resolvedAbsoluteComponentUrl, String.class);
        if (StringUtils.isBlank(htmlSnippet)) {
            throw new AppIntegrationException("Cannot load html-snippet " + resolvedAbsoluteComponentUrl);
        }

        return htmlSnippet;
    }

    private String resolveUrl(String url, List<ContextProvider<I>> contextProviderList, I instance) {
        Map<String, String> context = new HashMap<>();
        for (ContextProvider<I> contextProvider : contextProviderList) {
            context.putAll(contextProvider.getContext(instance));
        }
        return StringSubstitutor.replace(url, context);
    }



    @Nonnull
    private ApplicationRecord getApplicationRecord(@Nonnull String applicationId) {
        return Optional.ofNullable(applicationRecordMap.get(applicationId))
                .orElseThrow(() -> new AppIntegrationException("Unknown application-id " + applicationId));
    }


    public void registerResourceLoader(@Nonnull String id, @Nonnull ResourceLoader resourceLoader) {
        resourceLoaderMap.put(id, resourceLoader);
    }

    public void unregisterResourceLoader(@Nonnull String id) {
        resourceLoaderMap.remove(id);
    }

      public void registerContextProvider(String id, ContextProvider<I> contextProvider) {
        contextProviderMap.put(id, contextProvider);
    }

    public void unregisterContextProvider(String id) {
        contextProviderMap.remove(id);
    }


    private class ApplicationRecord {

        private final String applicationId;

        private final Application application;

        private ResourceLoader resourceLoader = null;

        private List<ContextProvider<I>> contextProviderList = null;

        public ApplicationRecord(String applicationId, Application application) {
            this.applicationId = applicationId;
            this.application = application;
        }

        public String getApplicationInfoUrl() {
            return application.getApplicationInfoUrl();
        }

        public void clearResourceLoader(String resourceLoaderId) {
            if (StringUtils.equals(resourceLoaderId, application.getUsedResourceLoader())) {
                this.resourceLoader = null;
            }
        }

        public ResourceLoader getResourceLoader() {
            if (this.resourceLoader == null) {
                final String resourceLoaderId = application.getUsedResourceLoader();
                if (StringUtils.isBlank(resourceLoaderId)) {
                    throw new AppIntegrationException("Application " + applicationId + " has no resource-loader defined");
                }
                this.resourceLoader = Optional.ofNullable(resourceLoaderMap.get(resourceLoaderId))
                        .orElseThrow(() -> new AppIntegrationException("Unsupported resource-loader " + resourceLoaderId + " of application " + applicationId));
            }
            return this.resourceLoader;
        }

        public String resolveRelativeUrl(String relativeUrl) {
            return null;
            //return this.getResourceLoader().resolveRelativeUrl(application.getApplicationInfoUrl(), relativeUrl);
        }

        public void clearContextProvider(String contextProviderId) {
            if ((application.getUsedContextProviders() != null) && (application.getUsedContextProviders().contains(contextProviderId))) {
                this.contextProviderList = null;
            }
        }

        public List<ContextProvider<I>> getContextProviders() {
            if (this.contextProviderList == null) {
                final List<String> usedContextProviderIdList = application.getUsedContextProviders();
                if ((usedContextProviderIdList == null) || usedContextProviderIdList.isEmpty()) {
                    this.contextProviderList = Collections.emptyList();
                } else {
                    // verify, all context providers exists
                    final String unsupportedContextProviders = usedContextProviderIdList.stream()
                            .filter(id -> !contextProviderMap.containsKey(id))
                            .collect(Collectors.joining(", "));

                    if (StringUtils.isNotBlank(unsupportedContextProviders)) {
                        throw new AppIntegrationException("Unsupported context providers " + unsupportedContextProviders + " of application " + applicationId);
                    }

                    this.contextProviderList = usedContextProviderIdList.stream().map(contextProviderMap::get).collect(Collectors.toList());
                }
            }
            return this.contextProviderList;
        }
    }
}
