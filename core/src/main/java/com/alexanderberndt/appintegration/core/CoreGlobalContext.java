package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.valuemap.RankedAndTypedValueMap;

import javax.annotation.Nonnull;

public class CoreGlobalContext extends GlobalContext {

    public CoreGlobalContext(ResourceLoader resourceLoader, RankedAndTypedValueMap globalParams) {
        super(resourceLoader, globalParams);
    }

    @Override
    public <T extends GenericTask<T>> TaskContext<T> createTaskContext(@Nonnull T task, @Nonnull String taskNamespace) {
        return new CoreTaskContext<>(this, task, taskNamespace, task.getHumanReadableName());
    }

}
