package com.alexanderberndt.appintegration.pipeline.builder.simple;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskWrapper;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
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

    private TaskWrapper<LoadingTask> loadingTask;

    private final List<TaskWrapper<ProcessingTask>> processingTasks = new ArrayList<>();


    public ProcessingPipeline build() {
        if (loadingTask == null) {
            throw new AppIntegrationException("Failed to create pipeline, as it MUST contain exactly one loading task!");
        }
        return new ProcessingPipeline(preparationTasks, loadingTask, processingTasks);
    }

    public SimplePipelineBuilder addPreparationTask(@Nonnull String taskId, @Nonnull PreparationTask task) {
        if (loadingTask == null) {
            this.currentTaskConfig = new DataMap();
            preparationTasks.add(new TaskWrapper<>(taskId, taskId, task, this.currentTaskConfig));
            return this;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. After adding a LoadingTask, only ProcessingTasks can be added.", taskId));
        }
    }

    public SimplePipelineBuilder addLoadingTask(@Nonnull String taskId, @Nonnull LoadingTask task) {
        if (loadingTask == null) {
            this.currentTaskConfig = new DataMap();
            loadingTask = new TaskWrapper<>(taskId, taskId, task, this.currentTaskConfig);
            return this;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. Only one LoadingTask can be added.", taskId));
        }
    }

    public SimplePipelineBuilder addProcessingTask(@Nonnull String taskId, @Nonnull ProcessingTask task) {
        if (loadingTask != null) {
            this.currentTaskConfig = new DataMap();
            processingTasks.add(new TaskWrapper<>(taskId, taskId, task, this.currentTaskConfig));
            return this;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added. A LoadingTask must be added before any ProcessingTasks.", taskId));
        }
    }

    public SimplePipelineBuilder withTaskParam(@Nonnull String param, Object value) {
        Objects.requireNonNull(this.currentTaskConfig, "Add a task before setting configuration parameters!");
        this.currentTaskConfig.setData(param, value);
        return this;
    }

}
