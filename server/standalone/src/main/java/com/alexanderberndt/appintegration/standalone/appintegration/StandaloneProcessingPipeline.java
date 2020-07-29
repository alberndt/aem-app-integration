package com.alexanderberndt.appintegration.standalone.appintegration;

import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;

import java.util.Map;

public class StandaloneProcessingPipeline extends ProcessingPipeline<StandaloneProcessingContext> {

    public StandaloneProcessingPipeline(TaskFactory<StandaloneProcessingContext> taskFactory, Map<String, Object> globalProperties) {
        super(taskFactory, globalProperties);
    }
}
