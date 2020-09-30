package com.alexanderberndt.appintegration.tasks.process;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;

@Component
public class AddReferencedResourceTask implements ProcessingTask {

    @Override
    public void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource) {
        final String relativeUrl = taskContext.getValue("relativeUrl", String.class);

        if (StringUtils.isNotBlank(relativeUrl)) {
            final String expectedTypeAsString = taskContext.getValue("expectedType", String.class);
            final ExternalResourceType expectedType = ExternalResourceType.parse(expectedTypeAsString);
            resource.addReference(relativeUrl, expectedType);
        }
    }
}
