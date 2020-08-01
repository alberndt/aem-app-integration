package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoaderFactory;
import com.alexanderberndt.appintegration.pipeline.valuemap.ScopedValueMapFacade;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public abstract class TaskContext implements Context {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GlobalContext globalContext;

    private final String contextId;

    private final String contextName;

    private final ValueMap taskParams;

    private final String messagePrefix;

    protected TaskContext(@Nonnull GlobalContext globalContext, String contextId, String contextName, ValueMap taskParams) {
        this.globalContext = globalContext;
        this.contextId = contextId;
        this.contextName = contextName;
        // ToDo: Connect with parent-context
        this.taskParams = taskParams;
        this.messagePrefix = String.format("%s (%s): ", contextName, contextId);
    }

    public ScopedValueMapFacade getTaskParams() {
        return new ScopedValueMapFacade(this, taskParams);
    }

    public void addWarning(String message) {
        LOG.warn("{}{}", messagePrefix, message);
    }

    public void addError(String message) {
        LOG.error("{}{}", messagePrefix, message);
    }

    @Nonnull
    public ResourceLoaderFactory getResourceLoaderFactory() {
        throw new UnsupportedOperationException("Not yet implemented!");
        //return globalContext.getResourceLoaderFactory();
    }

}
