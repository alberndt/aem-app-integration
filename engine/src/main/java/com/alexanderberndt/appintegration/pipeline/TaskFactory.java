package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public abstract class TaskFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, PreparationTask> preparationTaskMap = new HashMap<>();

    private final Map<String, ProcessingTask> processingTaskMap = new HashMap<>();

    @Nullable
    public final PreparationTask getPreparationTask(@Nonnull String name) {
        return preparationTaskMap.get(name);
    }

    @Nullable
    public final ProcessingTask getProcessingTask(@Nonnull String name) {
        return processingTaskMap.get(name);
    }

    protected void register(@Nonnull final PreparationTask task, @Nullable final String nameHint) {
        final String taskName = getTaskName(task.getClass(), nameHint);
        LOG.info("register preparation-task {} of class {}", taskName, task.getClass());
        preparationTaskMap.put(taskName, task);
    }

    protected void register(@Nonnull final ProcessingTask task, @Nullable final String nameHint) {
        final String taskName = getTaskName(task.getClass(), nameHint);
        LOG.info("register processing-task {} of class {}", taskName, task.getClass());
        processingTaskMap.put(taskName, task);
    }

    protected void unregister(@Nonnull final PreparationTask task) {
        preparationTaskMap.entrySet().removeIf(entry -> (entry.getValue() == task));
    }

    protected void unregister(@Nonnull final ProcessingTask task) {
        processingTaskMap.entrySet().removeIf(entry -> (entry.getValue() == task));
    }

    private String getTaskName(@Nonnull Class<?> taskClass, @Nullable String nameHint) {
        if (StringUtils.isNotBlank(nameHint)) {
            return nameHint;
        } else {
            // derive task-name from the implementing class
            // (remove ending Task, and convert camel-case to kebab-case
            return taskClass.getSimpleName()
                    .replaceAll("Task$", "")
                    .replaceAll("([a-z])([A-Z])", "$1-$2")
                    .toLowerCase();
        }
    }
}
