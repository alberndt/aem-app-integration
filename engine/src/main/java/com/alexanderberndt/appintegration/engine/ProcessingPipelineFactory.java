package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;

/**
 * Factory for processing pipelines, which has to be implemented for each runtime environment (e.g. Standalone or Sling/AEM).
 *
 * @see AppIntegrationFactory
 * @see AppIntegrationEngine
 */
public interface ProcessingPipelineFactory {

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param context Global processing context
     * @param name    Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    ProcessingPipeline createProcessingPipeline(@Nonnull final GlobalContext context, @Nonnull final String name);

}
