package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public abstract class GlobalContext implements Context {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResourceLoader resourceLoader;

    private final ValueMap globalParams;

    protected GlobalContext(ResourceLoader resourceLoader, ValueMap globalParams) {
        this.resourceLoader = resourceLoader;
        this.globalParams = globalParams;
    }

    public abstract TaskContext createChildContext(String contextId, String contextName, ValueMap parametersMap);

    @Nonnull
    public final ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public final ValueMap getGlobalParams() {
        return globalParams;
    }

}
