package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.api.task.GenericTask;

public interface TaskFactory {

    GenericTask getTask(String name);

}
