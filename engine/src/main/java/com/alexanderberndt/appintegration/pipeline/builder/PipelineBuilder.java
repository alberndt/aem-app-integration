package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.engine.logging.ResourceLog;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;

import javax.annotation.Nonnull;

public class PipelineBuilder {

    private final TaskFactory taskFactory;

    private final BasicPipelineBuilder innerBuilder;


    public PipelineBuilder(@Nonnull GlobalContext context, @Nonnull TaskFactory taskFactory, @Nonnull ResourceLog pipelineLog) {
        this.taskFactory = taskFactory;
        this.innerBuilder = new BasicPipelineBuilder(context, pipelineLog);
    }

    public PipelineBuilder addTask(@Nonnull String taskName) {
        innerBuilder.addTask(requireTask(taskName));
        return this;
    }

    public PipelineBuilder addTask(@Nonnull String taskName, @Nonnull String uniqueTaskId) {
        innerBuilder.addTask(requireTask(taskName), uniqueTaskId);
        return this;
    }

    public PipelineBuilder withTaskParam(String param, Object value) {
        innerBuilder.withTaskParam(param, value);
        return this;
    }

    public ProcessingPipeline build() {
        return innerBuilder.build();
    }

    @Nonnull
    private GenericTask requireTask(@Nonnull String taskName) {
        GenericTask task = taskFactory.getTask(taskName);
        if (task == null) {
            throw new IllegalArgumentException(String.format("Unknown task %s", taskName));
        }
        return task;
    }
}