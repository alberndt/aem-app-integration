package com.alexanderberndt.appintegration.api.task;

public interface GenericTask {

    default String getName() {
        return this.getClass().getSimpleName()
                .replaceAll("Task$", "")
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }

}
