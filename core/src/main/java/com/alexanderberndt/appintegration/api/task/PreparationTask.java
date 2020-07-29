package com.alexanderberndt.appintegration.api.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.TaskContext;

public interface PreparationTask extends GenericTask {

    void prepare(TaskContext context, ExternalResourceRef resourceRef);

}
