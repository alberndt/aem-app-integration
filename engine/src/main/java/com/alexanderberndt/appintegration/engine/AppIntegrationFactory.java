package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParserSupplier;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public interface AppIntegrationFactory<I extends ApplicationInstance, C extends GlobalContext<I, C>> {

    @Nonnull
    Map<String, Application> getAllApplications();

    @Nonnull
    Map<String, ResourceLoader> getAllResourceLoaders();

    @Nonnull
    Map<String, ContextProvider<I>> getAllContextProvider();

    @Nonnull
    List<TextParser> getAllTextParsers();

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param name Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    ProcessingPipeline createProcessingPipeline(@Nonnull C context, @Nonnull final String name);

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

    @Nonnull
    default ExternalResourceFactory getExternalResourceFactory() {
        final TextParserSupplier supplier = this::getAllTextParsers;
        return new ExternalResourceFactory() {
            @Nonnull
            @Override
            public ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, @Nullable DataMap metadataMap) {
                return new ExternalResource(uri, type, content, metadataMap, supplier);
            }
        };
    }
}
