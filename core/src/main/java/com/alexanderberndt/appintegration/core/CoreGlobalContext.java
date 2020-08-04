package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;

import javax.annotation.Nonnull;

public class CoreGlobalContext extends GlobalContext {

    public CoreGlobalContext(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public CoreTaskContext createTaskContext(@Nonnull Ranking rank, @Nonnull String taskNamespace) {
        return new CoreTaskContext(this, rank, taskNamespace);
    }

}
