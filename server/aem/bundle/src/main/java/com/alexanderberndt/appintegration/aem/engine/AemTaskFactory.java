package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import org.osgi.service.component.annotations.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class AemTaskFactory implements TaskFactory {

    private final Map<String, GenericTask> taskMap = new HashMap<>();

    @Override
    public GenericTask getTask(String name) {
        return taskMap.get(name);
    }

    @Reference(name = "task", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindTask(final GenericTask task, Map<String,?> properties) {
        taskMap.put(getTaskName(task), task);
    }

    protected void unbindTask(final GenericTask task) {
        taskMap.remove(getTaskName(task));
    }

    protected String getTaskName(final GenericTask task) {
        return task.getName();
    }

}
