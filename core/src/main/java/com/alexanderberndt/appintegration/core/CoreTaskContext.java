package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

public final class CoreTaskContext extends TaskContext {


    protected CoreTaskContext(GlobalContext globalContext, Ranking rank, String taskNamespace, ExternalResourceType resourceType) {
        super(globalContext, rank, taskNamespace, resourceType);
    }
}
