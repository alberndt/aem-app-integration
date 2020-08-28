package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.osgi.service.component.annotations.Component;

@Component(service = {PreparationTask.class, GenericTask.class})
public class PropertiesTask implements PreparationTask {

    @Override
    public String getName() {
        return "properties";
    }

    @Override
    public void prepare(TaskContext context, ExternalResourceRef resourceRef) {
        for (String key : context.keySet()) {
            context.setValue(key, context.getValue(key));
        }
    }

}
