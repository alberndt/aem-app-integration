package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;

public class TestGlobalContext extends GlobalContext implements ExternalResourceFactory {

    public TestGlobalContext(@Nonnull final LogAppender logAppender) {
        super(logAppender);
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskId, @Nonnull ExternalResourceType resourceType, @Nonnull DataMap executionDataMap) {
        return new TestTaskContext(this, taskLogger, rank, taskId, resourceType, executionDataMap);
    }

    @Nonnull
    public ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, @Nullable DataMap metadataMap) {
        return new ExternalResource(uri, type, content, metadataMap, () -> Collections.singletonList(new StringConverter()));
    }


}
