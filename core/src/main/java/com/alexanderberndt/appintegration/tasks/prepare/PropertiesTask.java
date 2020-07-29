package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.api.task.PreparationTask;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.ProcessingContext;

public class PropertiesTask implements PreparationTask {

    @Override
    public String getName() {
        return "properties";
    }

    @Override
    public void prepare(ProcessingContext context, ExternalResourceRef resourceRef) {
        resourceRef.getProperties().putAll(context.getParametersMap().getParentPredefinedValues());
        resourceRef.getProperties().putAll(context.getParametersMap());
    }

}
