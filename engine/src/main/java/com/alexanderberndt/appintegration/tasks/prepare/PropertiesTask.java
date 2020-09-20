package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;

@Component
public class PropertiesTask implements PreparationTask {

    @Override
    public void prepare(@Nonnull TaskContext context, @Nonnull ExternalResourceRef resourceRef) {
        context.getConfiguration().forEach(context::setValue);
    }

}
