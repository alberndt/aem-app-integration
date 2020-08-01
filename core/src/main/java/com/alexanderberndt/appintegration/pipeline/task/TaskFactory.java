package com.alexanderberndt.appintegration.pipeline.task;

public interface TaskFactory {

    GenericTask getTask(String name);

}
