package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.pipeline.TaskContext;
import com.alexanderberndt.appintegration.utils.ValueMap;

public final class CoreTaskContext extends TaskContext {

    CoreTaskContext(CoreGlobalContext globalCtx, String contextId, String contextName, ValueMap parametersMap) {
        super(globalCtx, contextId, contextName, parametersMap);
    }
}
