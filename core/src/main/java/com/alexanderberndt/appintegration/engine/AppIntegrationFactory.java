package com.alexanderberndt.appintegration.engine;

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
