package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;

import javax.annotation.Nonnull;
import java.io.InputStream;

public class ProcessingPipelineBuilder<I> {

    private Object currentPipelineOutput;
    private Class<?> currentPipelineOutputType = InputStream.class;

    public ProcessingPipelineBuilder(@Nonnull I input) {
        this.currentPipelineOutput = input;
    }

    @SuppressWarnings("unchecked")
    public void appendFilter(ProcessingContext context, PipelineFilter<?, ?> filter) {

        if (!filter.getInputType().isAssignableFrom(currentPipelineOutputType)) {
            throw new AppIntegrationException("Expected input-type of " + filter.getInputType().getSimpleName()
                    + " is not assignable from " + currentPipelineOutputType.getSimpleName());
        }

        currentPipelineOutput = ((PipelineFilter<Object, Object>) filter).filter(context, currentPipelineOutput);
        currentPipelineOutputType = filter.getOutputType();
    }


    public Object getCurrentPipelineOutput() {
        return currentPipelineOutput;
    }

    public Class<?> getCurrentPipelineOutputType() {
        return currentPipelineOutputType;
    }
}
