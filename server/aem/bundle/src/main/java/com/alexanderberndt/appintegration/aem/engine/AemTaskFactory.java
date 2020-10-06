package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.*;

import javax.annotation.Nullable;
import java.util.Map;

@Component(service = TaskFactory.class)
public class AemTaskFactory extends TaskFactory {

    public static final String TASK_NAME_PROPERTY = "task-name";

    @Reference(name = "preparationTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindPreparationTask(final PreparationTask task, Map<String, ?> properties) {
        super.registerPreparationTask(task, getTaskName(properties));
    }

    @SuppressWarnings("unused")
    protected void unbindPreparationTask(final PreparationTask task) {
        super.unregisterPreparationTask(task);
    }

    @Reference(name = "processingTask", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindProcessingTask(final ProcessingTask task, Map<String, ?> properties) {
        super.registerProcessingTask(task, getTaskName(properties));
    }

    @SuppressWarnings("unused")
    protected void unbindProcessingTask(final ProcessingTask task) {
        super.unregisterProcessingTask(task);
    }

    private String getTaskName(@Nullable Map<String, ?> properties) {

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

        return null;
    }
}
