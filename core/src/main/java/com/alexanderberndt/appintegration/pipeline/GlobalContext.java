package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoaderFactory;
import com.alexanderberndt.appintegration.utils.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public abstract class GlobalContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ValueMap globalParams;

    protected GlobalContext(ValueMap globalParams) {
        this.globalParams = globalParams;
    }

    public abstract TaskContext createChildContext(String contextId, String contextName, ValueMap parametersMap);

    @Nonnull
    public abstract ResourceLoaderFactory getResourceLoaderFactory();

    public ValueMap getGlobalParams() {
        return globalParams;
    }

}
