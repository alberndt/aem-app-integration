package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TaskWrapper<T> {

    @Nonnull
    private final String id;

    @Nonnull
    private final String name;

    @Nonnull
    private final T task;

    @Nullable
    private final DataMap configuration;

    public TaskWrapper(@Nonnull String id, @Nonnull String name, @Nonnull T task, @Nullable DataMap configuration) {
        this.id = id;
        this.name = name;
        this.task = task;
        this.configuration = configuration;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public T getTask() {
        return task;
    }

    @Nullable
    public DataMap getConfiguration() {
        return configuration;
    }
}
