package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

public class CoreTestGlobalContext extends GlobalContext {

    public CoreTestGlobalContext(ResourceLoader resourceLoader, @Nonnull final LogAppender logAppender) {
        super(resourceLoader, logAppender);
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType, @Nonnull Map<String, Object> processingData) {
        return new CoreTestTaskContext(this, taskLogger, rank, taskNamespace, resourceType, processingData);
    }

    @Override
    public void close() throws IOException {
    }
}
