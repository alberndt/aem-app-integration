package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface AppIntegrationFactory<I extends ApplicationInstance> {

    @Nonnull
    Map<String, Application> getAllApplications();

    @Nullable
    Application getApplication(@Nonnull String id);

    @Nullable
    ResourceLoader getResourceLoader(String id);

    @Nonnull
    Map<String, ResourceLoader> getAllResourceLoaders();

    @Nonnull
    ProcessingPipelineFactory getProcessingPipelineFactory();

    @Nullable
    ContextProvider<I> getContextProvider(@Nonnull final String providerName);

    @Nonnull
    Collection<TextParser> getAllTextParsers();

}
