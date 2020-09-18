package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class ResourceTypeByFileExtensionTask implements PreparationTask {

    @Override
    public void prepare(@Nonnull final TaskContext context, @Nonnull final ExternalResourceRef resourceRef) {
        final String path = StringUtils.defaultString(resourceRef.getUri().getPath(), "");
        final String extension = StringUtils.substringAfterLast(path, ".").toLowerCase();

        final ExternalResourceType resourceType;
        switch (extension) {
            case "css":
                resourceType = ExternalResourceType.CSS;
                break;
            case "txt":
                resourceType = ExternalResourceType.TEXT;
                break;
            case "js":
                resourceType = ExternalResourceType.JAVASCRIPT;
                break;
            default:
                resourceType = ExternalResourceType.ANY;
        }

        if (resourceType.isSpecializationOf(resourceRef.getExpectedType())) {
            resourceRef.setExpectedType(resourceType);
        }
    }

    /**
     * <p>Implementing classes should define a set of task-properties. This should be done by calling
     * {@link TaskContext#setValue(String, Object)} and {@link TaskContext#setType(String, Class)}. Although these
     * defaults can be overwritten, this ensures that they have a defined type and meaningful default values.</p>
     *
     * <p>With {@link TaskContext#setKeyComplete()} can be assured, that only known properties can be overwritten.
     * A warning is created, if a new property is created. This helps, that not accidentally wrong properties are specified.</p>
     *
     * @param taskContext TaskContext
     */
    @Override
    public void declareTaskPropertiesAndDefaults(@Nonnull TaskContext taskContext) {

    }
}
