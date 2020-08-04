package com.alexanderberndt.appintegration.pipeline.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

public interface PreparationTask extends GenericTask<PreparationTask> {

    void prepare(TaskContext<PreparationTask> context, ExternalResourceRef resourceRef);

}
