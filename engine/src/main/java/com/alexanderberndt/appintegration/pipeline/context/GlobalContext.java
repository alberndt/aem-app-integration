package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLog;
import com.alexanderberndt.appintegration.engine.logging.TaskLog;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public abstract class GlobalContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final ResourceLoader resourceLoader;

    @Nonnull
    private final PipelineConfiguration processingParams = new PipelineConfiguration();

    @Nonnull
    private final IntegrationLog integrationLog = new IntegrationLog();


    protected GlobalContext(@Nonnull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Nonnull
    public abstract TaskContext createTaskContext(
            @Nonnull TaskLog taskLog,
            @Nonnull Ranking rank,
            @Nonnull String taskNamespace,
            @Nonnull ExternalResourceType resourceType,
            @Nullable Map<String, Object> processingData);

    @Nonnull
    public final ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Nonnull
    public final PipelineConfiguration getProcessingParams() {
        return processingParams;
    }

    @Nonnull
    public IntegrationLog getIntegrationLog() {
        return integrationLog;
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
