package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface AppIntegrationFactory<I extends ApplicationInstance, C extends GlobalContext> {

    @Nonnull
    Map<String, Application> getAllApplications();

    @Nonnull
    Map<String, ResourceLoader> getAllResourceLoaders();

    @Nonnull
    Map<String, ContextProvider<I>> getAllContextProvider();

    @Nonnull
    Collection<TextParser> getAllTextParsers();

    @Nonnull
    ProcessingPipelineFactory<C> getProcessingPipelineFactory();

    @Nullable
    default Application getApplication(@Nonnull String id) {
        return getAllApplications().get(id);
    }

    @Nullable
    default ResourceLoader getResourceLoader(String id) {
        return getAllResourceLoaders().get(id);
    }

    @Nullable
    default ContextProvider<I> getContextProvider(@Nonnull final String providerName) {
        return getAllContextProvider().get(providerName);
    }

}
