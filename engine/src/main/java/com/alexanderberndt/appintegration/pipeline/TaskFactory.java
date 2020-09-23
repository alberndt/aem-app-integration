package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TaskFactory {

    @Nullable
    PreparationTask getPreparationTask(@Nonnull String name);

    @Nullable
    LoadingTask getLoadingTask(@Nonnull String name);

    @Nullable
    ProcessingTask getProcessingTask(@Nonnull String name);

    default String getDefaultTaskName(Class<?> taskClass) {
        // derive task-name from the implementing class
        // (remove ending Task, and convert camel-case to kebab-case
        return taskClass.getSimpleName()
                .replaceAll("Task$", "")
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }
}
