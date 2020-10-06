package com.alexanderberndt.appintegration.pipeline.builder;


import javax.annotation.Nullable;
import java.util.Map;

public interface PipelineDefinition {

    @Nullable
    Map<String, TaskDefinition> getPreparationTasks();

    @Nullable
    Map<String, TaskDefinition> getProcessingTasks();

}
