package com.alexanderberndt.appintegration.pipeline.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

public interface LoadingTask {


    ExternalResource load(TaskContext context, ExternalResourceRef resourceRef);

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
    default void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
    }

}
