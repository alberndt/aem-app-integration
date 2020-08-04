package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.pipeline.task.GenericTask;

public class TaskInstance<T extends GenericTask> {

    private final T task;

    private final String taskNamespace;

    public TaskInstance(T task) {
        this(task, task.getName());
    }

    public TaskInstance(T task, String taskNamespace) {
        this.task = task;
        this.taskNamespace = taskNamespace;
    }

    public T getTask() {
        return task;
    }

    public String getTaskNamespace() {
        return taskNamespace;
    }
}
