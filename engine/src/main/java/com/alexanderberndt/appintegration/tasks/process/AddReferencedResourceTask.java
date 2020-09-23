package com.alexanderberndt.appintegration.tasks.process;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class AddReferencedResourceTask implements ProcessingTask {

    @Override
    public void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource) {
        String relativeUrl = taskContext.getValue("relativeUrl", String.class);
        String expectedTypeAsString = taskContext.getValue("expectedType", String.class);

        final ExternalResourceType expectedType;
        if (StringUtils.isNotBlank(expectedTypeAsString)) {
            expectedType = ExternalResourceType.parse(expectedTypeAsString);
        } else {
            expectedType = null;
        }

        resource.addReference(relativeUrl, expectedType);
    }
}
