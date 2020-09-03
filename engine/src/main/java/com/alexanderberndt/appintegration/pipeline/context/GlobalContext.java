package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public abstract class GlobalContext implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nullable
    private ResourceLoader resourceLoader;

    @Nonnull
    private final PipelineConfiguration processingParams = new PipelineConfiguration();

    @Nonnull
    private final IntegrationLogger logger;

    protected GlobalContext(@Nonnull LogAppender logAppender) {
        this.logger = new IntegrationLogger(logAppender);
    }

    @Nonnull
    public abstract TaskContext createTaskContext(
            @Nonnull TaskLogger taskLogger,
            @Nonnull Ranking rank,
            @Nonnull String taskNamespace,
            @Nonnull ExternalResourceType resourceType,
            @Nonnull Map<String, Object> processingData);

    @Nullable
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Nonnull
    public final PipelineConfiguration getProcessingParams() {
        return processingParams;
    }

    @Nonnull
    public IntegrationLogger getIntegrationLog() {
        return logger;
    }

    @Deprecated
    public void addWarning(String message) {
        LOG.warn("{}", message);
    }

    @Deprecated
    public void addError(String message) {
        LOG.error("{}", message);
    }

}
