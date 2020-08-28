package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.*;
import com.alexanderberndt.appintegration.engine.loader.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJsonParser;
import com.alexanderberndt.appintegration.pipeline.SystemResourcePipelineFactory;
import com.alexanderberndt.appintegration.tasks.CoreTaskFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CoreAppIntegrationFactory implements AppIntegrationFactory<CoreTestAppInstance> {

    public static final String SYSTEM_RESOURCE_LOADER_NAME = "classpath";
    public static final String HTTP_RESOURCE_LOADER_NAME = "http";

    public static final List<String> CORE_CONTEXT_PROVIDERS = Collections.singletonList("instance");

    private final Map<String, Application> applicationMap = new HashMap<>();

    private final Map<String, ResourceLoader> resourceLoaderMap;

    private final ProcessingPipelineFactory processingPipelineFactory;

    private final Map<String, ContextProvider<CoreTestAppInstance>> contextProviderMap;

    private final List<TextParser> textParsers;

    public CoreAppIntegrationFactory() {
        resourceLoaderMap = new HashMap<>();
        resourceLoaderMap.put(SYSTEM_RESOURCE_LOADER_NAME, new SystemResourceLoader());
        resourceLoaderMap.put(HTTP_RESOURCE_LOADER_NAME, new HttpResourceLoader());

        processingPipelineFactory = new SystemResourcePipelineFactory(new CoreTaskFactory(), "local/pipelines");

        contextProviderMap = new HashMap<>();
        contextProviderMap.put("instance", CoreTestAppInstance::getContextMap);

        textParsers = new ArrayList<>();
        textParsers.add(new StringConverter());
        textParsers.add(new ApplicationInfoJsonParser());
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
    public Map<String, ResourceLoader> getAllResourceLoaders() {
        return Collections.unmodifiableMap(resourceLoaderMap);
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

    @Nonnull
    @Override
    public Collection<TextParser> getAllTextParsers() {
        return textParsers;
    }

    public void registerApplication(@Nonnull String id, @Nonnull Application application) {
        applicationMap.put(id, application);
    }

    public void unregisterApplication(@Nonnull String id) {
        applicationMap.remove(id);
    }

}
