package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Component
public class AemTaskFactory implements TaskFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, GenericTask> taskMap = new HashMap<>();

    @Override
    public GenericTask getTask(String name) {
        return taskMap.get(name);
    }

    @Reference(name = "genericTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindGenericTask(final GenericTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task);
        LOG.info("register generic-task {} of class {}", taskName, task.getClass());
        taskMap.put(taskName, task);
    }

    protected void unbindGenericTask(final GenericTask task) {
        taskMap.remove(getTaskName(task));
    }

    @Reference(name = "preparationTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindPreparationTask(final PreparationTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task);
        LOG.info("register preparation-task {} of class {}", taskName, task.getClass());
        taskMap.put(taskName, task);
    }

    protected void unbindPreparationTask(final PreparationTask task) {
        taskMap.remove(getTaskName(task));
    }

    @Reference(name = "loadingTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindLoadingTask(final LoadingTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task);
        LOG.info("register loading-task {} of class {}", taskName, task.getClass());
        taskMap.put(taskName, task);
    }

    protected void unbindLoadingTask(final LoadingTask task) {
        taskMap.remove(getTaskName(task));
    }

    @Reference(name = "processingTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindProcessingTask(final ProcessingTask task, Map<String, ?> properties) {
        final String taskName = getTaskName(task);
        LOG.info("register processing-task {} of class {}", taskName, task.getClass());
        taskMap.put(taskName, task);
    }

    protected void unbindProcessingTask(final ProcessingTask task) {
        taskMap.remove(getTaskName(task));
    }


    protected String getTaskName(final GenericTask task) {
        return task.getName();
    }

}
