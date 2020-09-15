package com.alexanderberndt.appintegration.pipeline.builder.yaml;

import com.alexanderberndt.appintegration.pipeline.builder.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.builder.TaskDefinition;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class YamlPipelineDefinition implements PipelineDefinition {

    @JsonProperty("prepare")
    private Map<String, YamlTaskDefinition> preparationTasks;

    @JsonProperty("load")
    private Map<String, YamlTaskDefinition> loaderTasks;

    @JsonProperty("process")
    private Map<String, YamlTaskDefinition> processingTasks;

    @Nullable
    @Override
    public Map<String, TaskDefinition> getPreparationTasks() {
        return (preparationTasks != null) ? new HashMap<>(preparationTasks) : null;
    }

    @Nullable
    @Override
    public Map<String, TaskDefinition> getLoaderTasks() {
        return (loaderTasks != null) ? new HashMap<>(loaderTasks) : null;
    }

    @Nullable
    @Override
    public Map<String, TaskDefinition> getProcessingTasks() {
        return (processingTasks != null) ? new HashMap<>(processingTasks) : null;
    }
}
