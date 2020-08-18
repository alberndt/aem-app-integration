package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.SystemResourceLoader;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipelineFactory;
import com.alexanderberndt.appintegration.pipeline.SystemResourcePipelineFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreAppIntegrationFactory implements AppIntegrationFactory<CoreTestAppInstance> {

    public static final String SYSTEM_RESOURCE_LOADER_NAME = "classpath";
    public static final String HTTP_RESOURCE_LOADER_NAME = "http";

    public static final List<String> CORE_CONTEXT_PROVIDERS = Collections.singletonList("instance");

    private final Map<String, Application> applicationMap = new HashMap<>();

    private final Map<String, ResourceLoader> resourceLoaderMap;

    private final ProcessingPipelineFactory processingPipelineFactory;

    private final Map<String, ContextProvider<CoreTestAppInstance>> contextProviderMap;

    public CoreAppIntegrationFactory() {
        resourceLoaderMap = new HashMap<>();
        resourceLoaderMap.put(SYSTEM_RESOURCE_LOADER_NAME, new SystemResourceLoader());
        resourceLoaderMap.put(HTTP_RESOURCE_LOADER_NAME, new HttpResourceLoader());

        processingPipelineFactory = new SystemResourcePipelineFactory(new CoreTaskFactory(), "local/pipelines");

        contextProviderMap = new HashMap<>();
        contextProviderMap.put("instance", CoreTestAppInstance::getContextMap);
    }

    @Override
    public @Nonnull
    Map<String, Application> getAllApplications() {
        return Collections.unmodifiableMap(applicationMap);
    }

    @Nullable
    @Override
    public Application getApplication(@Nonnull String id) {
        return applicationMap.get(id);
    }

    @Nullable
    @Override
    public ResourceLoader getResourceLoader(String id) {
        return resourceLoaderMap.get(id);
    }

    @Nonnull
    @Override
    public ProcessingPipelineFactory getProcessingPipelineFactory() {
        return processingPipelineFactory;
    }

    @Override
    public @Nullable
    ContextProvider<CoreTestAppInstance> getContextProvider(@Nonnull String providerName) {
        return contextProviderMap.get(providerName);
    }

    public void registerApplication(@Nonnull String id, @Nonnull Application application) {
        applicationMap.put(id, application);
    }

    public void unregisterApplication(@Nonnull String id) {
        applicationMap.remove(id);
    }

}
