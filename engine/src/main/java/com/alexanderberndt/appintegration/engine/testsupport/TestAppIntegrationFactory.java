package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ContextProvider;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJsonParser;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.SystemResourcePipelineFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TestAppIntegrationFactory implements AppIntegrationFactory<TestAppInstance, TestGlobalContext> {

    public static final String SYSTEM_RESOURCE_LOADER_NAME = "classpath";
    public static final String HTTP_RESOURCE_LOADER_NAME = "http";

    public static final List<String> CORE_CONTEXT_PROVIDERS = Collections.singletonList("instance");

    private final Map<String, Application> applicationMap = new HashMap<>();

    private final Map<String, ResourceLoader> resourceLoaderMap;

    private final SystemResourcePipelineFactory processingPipelineFactory;

    private final Map<String, ContextProvider<TestAppInstance>> contextProviderMap;

    private final List<TextParser> textParsers;

    public TestAppIntegrationFactory() {
        resourceLoaderMap = new HashMap<>();
        resourceLoaderMap.put(SYSTEM_RESOURCE_LOADER_NAME, new SystemResourceLoader());
        resourceLoaderMap.put(HTTP_RESOURCE_LOADER_NAME, new HttpResourceLoader());

        processingPipelineFactory = new SystemResourcePipelineFactory(new TestTaskFactory(), "local/pipelines");

        contextProviderMap = new HashMap<>();
        contextProviderMap.put("instance", TestAppInstance::getContextMap);

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


    @Override
    public @Nullable
    ContextProvider<TestAppInstance> getContextProvider(@Nonnull String providerName) {
        return contextProviderMap.get(providerName);
    }

    @Nonnull
    @Override
    public Map<String, ContextProvider<TestAppInstance>> getAllContextProvider() {
        return Collections.unmodifiableMap(contextProviderMap);
    }

    @Nonnull
    @Override
    public Collection<TextParser> getAllTextParsers() {
        return textParsers;
    }

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param context
     * @param name    Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    @Override
    public ProcessingPipeline createProcessingPipeline(TestGlobalContext context, @Nonnull String name) {
        return processingPipelineFactory.createProcessingPipeline(name);
    }

    public void registerApplication(@Nonnull String id, @Nonnull Application application) {
        applicationMap.put(id, application);
    }

    public void unregisterApplication(@Nonnull String id) {
        applicationMap.remove(id);
    }

}