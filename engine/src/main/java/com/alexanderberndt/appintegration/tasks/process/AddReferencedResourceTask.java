package com.alexanderberndt.appintegration.tasks.process;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;

public class AddReferencedResourceTask implements ProcessingTask {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void process(@Nonnull TaskContext context, @Nonnull ExternalResource resource) {
        String relativeUrl = context.getValue("relativeUrl", String.class);
        String expectedTypeAsString = context.getValue("expectedType", String.class);

        final ExternalResourceType expectedType;
        if (StringUtils.isNotBlank(expectedTypeAsString)) {
            expectedType = ExternalResourceType.parse(expectedTypeAsString);
        } else {
            expectedType = null;
        }

        try {
            resource.addReference(relativeUrl, expectedType);
        } catch (URISyntaxException e) {
            LOG.error("cannot create reference for {}", relativeUrl, e);
            context.addError("Cannot cannot create reference for %s due to %s!", relativeUrl, e.getMessage());
        }
    }
}
