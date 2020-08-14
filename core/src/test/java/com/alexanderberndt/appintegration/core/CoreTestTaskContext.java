package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

import javax.annotation.Nonnull;
import java.util.Map;

public final class CoreTestTaskContext extends TaskContext {

    protected CoreTestTaskContext(GlobalContext globalContext, Ranking rank, String taskNamespace, ExternalResourceType resourceType, @Nonnull Map<String, Object> executionDataMap) {
        super(globalContext, rank, taskNamespace, resourceType, executionDataMap);
    }
}
