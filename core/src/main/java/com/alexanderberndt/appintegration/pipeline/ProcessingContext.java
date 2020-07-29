package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.utils.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ProcessingContext {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ProcessingContext parentCtx;

    private final String contextId;

    private final String contextName;

    private final ValueMap parametersMap;

    private final String messagePrefix;

    public ProcessingContext(ValueMap parametersMap) {
        this(null, null, null, parametersMap);
    }

    public ProcessingContext(ProcessingContext parentCtx, String contextId, String contextName, ValueMap parametersMap) {
        this.parentCtx = parentCtx;
        this.contextId = contextId;
        this.contextName = contextName;
        // ToDo: Connect with parent-context
        this.parametersMap = parametersMap;
        this.messagePrefix = String.format("%s (%s): ", contextName, contextId);
    }

    public ProcessingContext createChildContext(String contextId, String contextName, ValueMap parametersMap) {
        return new ProcessingContext(this, contextId, contextName, parametersMap);
    }

    public ValueMap getParametersMap() {
        return parametersMap;
    }

    public void addWarning(String message) {
        LOG.warn("{}{}", messagePrefix, message);
    }

    public void addError(String message) {
        LOG.error("{}{}", messagePrefix, message);
    }

}
