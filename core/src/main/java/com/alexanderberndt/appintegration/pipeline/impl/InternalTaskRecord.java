package com.alexanderberndt.appintegration.pipeline.impl;

import com.alexanderberndt.appintegration.pipeline.context.TaskContext;

public class InternalTaskRecord<T> {

    private final TaskContext taskContext;

    private final T task;

    public InternalTaskRecord(TaskContext taskContext, T task) {
        this.taskContext = taskContext;
        this.task = task;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public T getTask() {
        return task;
    }

//    public TaskContext createChildContext(GlobalContext globalCtx) {
//        return globalCtx.createChildContext(taskName, "Task " + taskName, taskProperties);
//    }

}
