package com.alexanderberndt.appintegration.pipeline.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

public interface LoadingTask extends GenericTask {


    ExternalResource load(TaskContext context, ExternalResourceRef resourceRef, ExternalResourceFactory factory);

}
