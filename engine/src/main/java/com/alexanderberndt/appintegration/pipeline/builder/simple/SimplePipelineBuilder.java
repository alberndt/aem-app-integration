package com.alexanderberndt.appintegration.pipeline.builder.simple;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskWrapper;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimplePipelineBuilder {

    private DataMap currentTaskConfig;

    private final List<TaskWrapper<PreparationTask>> preparationTasks = new ArrayList<>();

    private final List<TaskWrapper<ProcessingTask>> processingTasks = new ArrayList<>();


    public ProcessingPipeline build() {
        return new ProcessingPipeline(preparationTasks, processingTasks);
    }

    public SimplePipelineBuilder addPreparationTask(@Nonnull String taskId, @Nonnull PreparationTask task) {
        if (processingTasks.isEmpty()) {
            this.currentTaskConfig = new DataMap();
            preparationTasks.add(new TaskWrapper<>(taskId, taskId, task, this.currentTaskConfig));
            return this;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. After adding a Processing Task, only ProcessingTasks can be added.", taskId));
        }
    }

    public SimplePipelineBuilder addProcessingTask(@Nonnull String taskId, @Nonnull ProcessingTask task) {
        this.currentTaskConfig = new DataMap();
        processingTasks.add(new TaskWrapper<>(taskId, taskId, task, this.currentTaskConfig));
        return this;
    }

    public SimplePipelineBuilder withTaskParam(@Nonnull String param, Object value) {
        Objects.requireNonNull(this.currentTaskConfig, "Add a task before setting configuration parameters!");
        this.currentTaskConfig.setData(param, value);
        return this;
    }

}
