package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap;

public final class CoreTaskContext extends TaskContext {

    CoreTaskContext(CoreGlobalContext globalCtx, String contextId, String contextName, ValueMap parametersMap) {
        super(globalCtx, contextId, contextName, parametersMap);
    }


    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public Ranking getRank() {
        return null;
    }


}
