package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogger;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.utils.VerifiedApplication;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;

public abstract class GlobalContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final VerifiedApplication verifiedApplication;

    @Nonnull final AppIntegrationEngine<?, ?> engine;

    @Nonnull
    private final PipelineConfiguration processingParams = new PipelineConfiguration();

    @Nonnull
    private final IntegrationLogger logger;


    protected GlobalContext(@Nonnull LogAppender logAppender, @Nonnull VerifiedApplication verifiedApplication, @Nonnull AppIntegrationEngine<?, ?> engine) {
        this.logger = new IntegrationLogger(logAppender);
        this.verifiedApplication = verifiedApplication;
        this.engine = engine;
    }

    @Nonnull
    public abstract TaskContext createTaskContext(
            @Nonnull TaskLogger taskLogger,
            @Nonnull Ranking rank,
            @Nonnull String taskId,
            @Nonnull ExternalResourceType resourceType,
            @Nullable DataMap processingData);

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return verifiedApplication.getResourceLoader();
    }

    @Nonnull
    public ExternalResourceFactory getResourceFactory() {
        // ToDo: move to engine
        return engine.getFactory().getExternalResourceFactory();
    }

    @Nonnull
    public final PipelineConfiguration getProcessingParams() {
        return processingParams;
    }

    @Nonnull
    public IntegrationLogger getIntegrationLog() {
        return logger;
    }

    /**
     *
     * @deprecated will be removed
     */
    @Deprecated
    public void addWarning(String message) {
        LOG.warn("{}", message);
    }

    @Deprecated
    public void addError(String message) {
        LOG.error("{}", message);
    }

}
