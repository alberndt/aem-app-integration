package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoaderFactory;
import com.alexanderberndt.appintegration.pipeline.GlobalContext;
import com.alexanderberndt.appintegration.utils.ValueMap;

import javax.annotation.Nonnull;

public class CoreGlobalContext extends GlobalContext {

    private final ResourceLoaderFactory resourceLoaderFactory = new CoreResourceLoaderFactory();

    public CoreGlobalContext(ValueMap globalParams) {
        super(globalParams);
    }

    @Override
    public CoreTaskContext createChildContext(String contextId, String contextName, ValueMap parametersMap) {
        return new CoreTaskContext(this, contextId, contextName, parametersMap);
    }

    @Override
    @Nonnull
    public ResourceLoaderFactory getResourceLoaderFactory() {
        return resourceLoaderFactory;
    }
}
