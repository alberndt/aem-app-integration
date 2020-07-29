package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.api.task.PreparationTask;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.TaskContext;

public class PropertiesTask implements PreparationTask {

    @Override
    public String getName() {
        return "properties";
    }

    @Override
    public void prepare(TaskContext context, ExternalResourceRef resourceRef) {
        resourceRef.getProperties().putAll(context.getTaskParams().getParentPredefinedValues());
        resourceRef.getProperties().putAll(context.getTaskParams());
    }

}
