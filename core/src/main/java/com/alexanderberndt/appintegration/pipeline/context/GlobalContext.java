package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.valuemap.RankedAndTypedValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public abstract class GlobalContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResourceLoader resourceLoader;

    private final RankedAndTypedValueMap processingParams;

    protected GlobalContext(ResourceLoader resourceLoader, RankedAndTypedValueMap processingParams) {
        this.resourceLoader = resourceLoader;
        this.processingParams = processingParams;
    }

    public abstract <T extends GenericTask<T>> TaskContext<T> createTaskContext(@Nonnull T task, @Nonnull String taskNamespace);

    @Nonnull
    public final ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public final RankedAndTypedValueMap getProcessingParams() {
        return processingParams;
    }

    public void addWarning(String message) {
        LOG.warn("{}", message);
    }

    public void addError(String message) {
        LOG.error("{}", message);
    }


}
