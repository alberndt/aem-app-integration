package com.alexanderberndt.appintegration.api.task;

import com.alexanderberndt.appintegration.pipeline.ProcessingContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

public interface PreparationTask extends GenericTask {

    void prepare(ProcessingContext context, ExternalResourceRef resourceRef);

}
