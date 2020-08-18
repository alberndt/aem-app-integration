package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;

public interface ProcessingPipelineFactory {

    ProcessingPipeline createProcessingPipeline(@Nonnull final GlobalContext context, @Nonnull final String name);

}
