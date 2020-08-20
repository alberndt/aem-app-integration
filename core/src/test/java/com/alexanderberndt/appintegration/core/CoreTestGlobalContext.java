package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.TaskLog;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

import javax.annotation.Nonnull;
import java.util.Map;

public class CoreTestGlobalContext extends GlobalContext {

    public CoreTestGlobalContext(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLog taskLog, @Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType, @Nonnull Map<String, Object> processingData) {
        return new CoreTestTaskContext(this, taskLog, rank, taskNamespace, resourceType, processingData);
    }

}
