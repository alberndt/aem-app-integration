package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AemGlobalContext extends GlobalContext<SlingApplicationInstance, AemGlobalContext> {

    @Nonnull
    private final ResourceResolver resourceResolver;

    public AemGlobalContext(@Nonnull String applicationId, @Nonnull AppIntegrationFactory<SlingApplicationInstance, AemGlobalContext> factory, @Nonnull ExternalResourceCache externalResourceCache, @Nullable LogAppender logAppender, @Nonnull ResourceResolver resourceResolver) {
        super(applicationId, factory, externalResourceCache, logAppender);
        this.resourceResolver = resourceResolver;
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskId, @Nonnull ExternalResourceType resourceType, @Nullable DataMap executionDataMap) {
        return new AemTaskContext(this, taskLogger, rank, taskId, resourceType, executionDataMap);
    }

    @Nonnull
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

}
