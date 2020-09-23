package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Component
public class AemTaskFactory implements TaskFactory {

    public static final String TASK_NAME_PROPERTY = "task-name";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, PreparationTask> preparationTaskMap = new HashMap<>();

    private final Map<String, LoadingTask> loadingTaskMap = new HashMap<>();

    private final Map<String, ProcessingTask> processingTaskMap = new HashMap<>();

    @Nullable
    @Override
    public PreparationTask getPreparationTask(@Nonnull String name) {
        return preparationTaskMap.get(name);
    }

    @Nullable
    @Override
    public LoadingTask getLoadingTask(@Nonnull String name) {
        return loadingTaskMap.get(name);
    }

    @Nullable
    @Override
    public ProcessingTask getProcessingTask(@Nonnull String name) {
        return processingTaskMap.get(name);
    }

    @Reference(name = "preparationTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindPreparationTask(final PreparationTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task.getClass(), properties);
        LOG.info("register preparation-task {} of class {}", taskName, task.getClass());
        preparationTaskMap.put(taskName, task);
    }

    protected void unbindPreparationTask(final PreparationTask task) {
        preparationTaskMap.entrySet().removeIf(entry -> (entry.getValue() == task));
    }

    @Reference(name = "loadingTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindLoadingTask(final LoadingTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task.getClass(), properties);
        LOG.info("register loading-task {} of class {}", taskName, task.getClass());
        loadingTaskMap.put(taskName, task);
    }

    protected void unbindLoadingTask(final LoadingTask task) {
        loadingTaskMap.entrySet().removeIf(entry -> (entry.getValue() == task));
    }

    @Reference(name = "processingTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindProcessingTask(final ProcessingTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task.getClass(), properties);
        LOG.info("register processing-task {} of class {}", taskName, task.getClass());
        processingTaskMap.put(taskName, task);
    }

    protected void unbindProcessingTask(final ProcessingTask task) {
        processingTaskMap.entrySet().removeIf(entry -> (entry.getValue() == task));
    }

    protected String getTaskName(@Nonnull final Class<?> taskClass, @Nullable Map<String, ?> properties) {

        // is task-name specified via a task-name property?
        if (properties != null) {
            Object taskNameObj = properties.get(TASK_NAME_PROPERTY);
            if (taskNameObj instanceof String) {
                String taskName = (String) taskNameObj;
                if (StringUtils.isNotBlank(taskName)) {
                    return taskName;
                }
            }
        }

        // fallback on default task-name
        return getDefaultTaskName(taskClass);
    }
}
