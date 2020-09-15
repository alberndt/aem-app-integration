package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.osgi.service.component.annotations.Component;

@Component
public class PropertiesTask implements PreparationTask {

    @Override
    public void prepare(TaskContext context, ExternalResourceRef resourceRef) {
        context.getConfiguration().forEach(context::setValue);
    }

}
