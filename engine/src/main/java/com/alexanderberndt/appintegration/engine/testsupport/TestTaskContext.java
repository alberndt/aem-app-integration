package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nullable;

public final class TestTaskContext extends TaskContext {

    protected TestTaskContext(GlobalContext globalContext, TaskLogger taskLogger, Ranking rank, String taskNamespace, ExternalResourceType resourceType, @Nullable DataMap executionDataMap) {
        super(globalContext, taskLogger, rank, taskNamespace, resourceType, executionDataMap);
    }
}
