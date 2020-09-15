package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

public class AemGlobalContext extends GlobalContext {

    @Nonnull
    private final ResourceResolver resourceResolver;

    protected AemGlobalContext(@Nonnull ResourceResolver resourceResolver, @Nonnull LogAppender logAppender) {
        super(logAppender);
        this.resourceResolver = resourceResolver;
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskId, @Nonnull ExternalResourceType resourceType, @Nonnull DataMap processingData) {
        return new AemTaskContext(this, taskLogger, rank, taskId, resourceType, processingData);
    }

    @Nonnull
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

}
