package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap;

public class CoreGlobalContext extends GlobalContext {

    public CoreGlobalContext(ResourceLoader resourceLoader, ValueMap globalParams) {
        super(resourceLoader, globalParams);
    }

    @Override
    public CoreTaskContext createChildContext(String contextId, String contextName, ValueMap parametersMap) {
        return new CoreTaskContext(this, contextId, contextName, parametersMap);
    }


    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public Ranking getRank() {
        return null;
    }

    @Override
    public void addWarning(String message) {

    }

    @Override
    public void addError(String message) {

    }
}
