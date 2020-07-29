package com.alexanderberndt.appintegration.standalone.appintegration;

import com.alexanderberndt.appintegration.pipeline.ProcessingContext;
import com.alexanderberndt.appintegration.utils.ValueMap;

public class StandaloneProcessingContext extends ProcessingContext<StandaloneProcessingContext> {


    public StandaloneProcessingContext(ValueMap parametersMap) {
        super(parametersMap);
    }

    public StandaloneProcessingContext(StandaloneProcessingContext parentCtx, String contextId, String contextName, ValueMap parametersMap) {
        super(parentCtx, contextId, contextName, parametersMap);
    }

    @Override
    public StandaloneProcessingContext createChildContext(String contextId, String contextName, ValueMap parametersMap) {
        return new StandaloneProcessingContext(this, contextId, contextName, parametersMap);
    }
}
