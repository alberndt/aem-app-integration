package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

import javax.annotation.Nonnull;
import java.util.Map;

public class AemTaskContext extends TaskContext {

    protected AemTaskContext(@Nonnull GlobalContext globalContext, @Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType, @Nonnull Map<String, Object> executionDataMap) {
        super(globalContext, taskLogger, rank, taskNamespace, resourceType, executionDataMap);
    }
}
