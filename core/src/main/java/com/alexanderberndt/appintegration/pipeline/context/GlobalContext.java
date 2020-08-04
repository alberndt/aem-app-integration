package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.valuemap.RankedAndTypedValueMap;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public abstract class GlobalContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResourceLoader resourceLoader;

    private final RankedAndTypedValueMap processingParams = new RankedAndTypedValueMap();

    protected GlobalContext(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public abstract TaskContext createTaskContext(@Nonnull Ranking rank, @Nonnull String taskNamespace);

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
