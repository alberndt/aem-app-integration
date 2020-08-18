package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipelineFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface AppIntegrationFactory<I extends ApplicationInstance> {

    @Nonnull
    Map<String, Application> getAllApplications();

    @Nullable
    Application getApplication(@Nonnull String id);

    @Nullable
    ResourceLoader getResourceLoader(String id);

    @Nonnull
    ProcessingPipelineFactory getProcessingPipelineFactory();

    @Nullable
    ContextProvider<I> getContextProvider(@Nonnull final String providerName);

}
