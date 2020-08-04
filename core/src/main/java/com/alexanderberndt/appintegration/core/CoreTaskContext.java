package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;

import javax.annotation.Nonnull;

public final class CoreTaskContext<T extends GenericTask<T>> extends TaskContext<T> {

    protected CoreTaskContext(@Nonnull GlobalContext globalContext, @Nonnull T task, @Nonnull String taskNamespace, String humanReadableTaskName) {
        super(globalContext, task, taskNamespace, humanReadableTaskName);
    }
}
