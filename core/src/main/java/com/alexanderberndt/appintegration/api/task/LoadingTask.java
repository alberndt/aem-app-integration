package com.alexanderberndt.appintegration.api.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.TaskContext;

public interface LoadingTask extends GenericTask {


    ExternalResource load(TaskContext context, ExternalResourceRef resourceRef);

}
