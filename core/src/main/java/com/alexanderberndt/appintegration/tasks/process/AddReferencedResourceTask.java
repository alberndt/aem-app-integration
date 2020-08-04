package com.alexanderberndt.appintegration.tasks.process;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class AddReferencedResourceTask implements ProcessingTask {

    @Override
    public void process(@Nonnull TaskContext context, ExternalResource resource) {
        String relativeUrl = context.getValue("relativeUrl", String.class);
        String expectedTypeAsString = context.getValue("expectedType", String.class);

        final ExternalResourceType expectedType;
        if (StringUtils.isNotBlank(expectedTypeAsString)) {
            expectedType = ExternalResourceType.parse(StringUtils.upperCase(expectedTypeAsString));
        } else {
            expectedType = null;
        }

        resource.addReference(relativeUrl, expectedType);
    }
}
