package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

public class AemGlobalContext extends GlobalContext {

    @Nonnull
    private final ResourceResolver resourceResolver;

    protected AemGlobalContext(@Nonnull ResourceResolver resourceResolver, @Nonnull LogAppender logAppender) {
        super(logAppender);
        this.resourceResolver = resourceResolver;
    }

    @Nonnull
    @Override
    public TaskContext createTaskContext(@Nonnull TaskLogger taskLogger, @Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType, @Nonnull Map<String, Object> processingData) {
        return new AemTaskContext(this, taskLogger, rank, taskNamespace, resourceType, processingData);
    }

    @Nonnull
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    @Override
    public void close() throws IOException {

    }
}
