package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.utils.VerifiedApplication;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestGlobalContext extends GlobalContext {

    public TestGlobalContext(@Nonnull final LogAppender logAppender, @Nonnull VerifiedApplication verifiedApplication, @Nonnull AppIntegrationEngine<?, ?> engine) {
        super(logAppender, verifiedApplication, engine);
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskId, @Nonnull ExternalResourceType resourceType, @Nullable DataMap executionDataMap) {
        return new TestTaskContext(this, taskLogger, rank, taskId, resourceType, executionDataMap);
    }
}
