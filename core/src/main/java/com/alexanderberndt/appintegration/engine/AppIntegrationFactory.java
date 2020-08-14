package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface AppIntegrationFactory<I extends ApplicationInstance> {

    Map<String, Application> getAllApplications();

    Application getApplication(@Nonnull String id);

    ResourceLoader getResourceLoader(String id);

    @Nullable
    ContextProvider<I> getContextProvider(@Nonnull final String providerName);

    ProcessingPipeline createProcessingPipeline(GlobalContext context, String name);
}
