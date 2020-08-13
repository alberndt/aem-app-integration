package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.pipeline.task.GenericTask;

public interface TaskFactory {

    GenericTask getTask(String name);

}
