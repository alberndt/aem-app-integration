package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

import javax.annotation.Nonnull;

public class CoreGlobalContext extends GlobalContext {

    public CoreGlobalContext(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public TaskContext createTaskContext(@Nonnull Ranking rank, @Nonnull String taskNamespace, @Nonnull ExternalResourceType resourceType) {
        return new CoreTaskContext(this, rank, taskNamespace, resourceType);
    }

}
