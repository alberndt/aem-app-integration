package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AemTaskContext extends TaskContext {

    protected AemTaskContext(@Nonnull GlobalContext globalContext, @Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType, @Nullable DataMap executionDataMap) {
        super(globalContext, taskLogger, rank, taskNamespace, resourceType, executionDataMap);
    }
}
