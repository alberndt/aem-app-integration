package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.tasks.load.DownloadTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import com.alexanderberndt.appintegration.tasks.process.FileSizeValidationTask;
import com.alexanderberndt.appintegration.tasks.process.RegexValidationTask;

import java.util.HashMap;
import java.util.Map;

public class CoreTaskFactory implements TaskFactory {

    private static final Map<String, GenericTask> taskMap = new HashMap<>();

    protected static void registerTask(GenericTask task) {
        registerTask(task.getName(), task);
    }

    protected static void registerTask(String name, GenericTask task) {
        taskMap.put(name, task);
    }

    static {
        registerTask(new PropertiesTask());
        registerTask(new DownloadTask());
        registerTask(new AddReferencedResourceTask());
        registerTask(new FileSizeValidationTask());
        registerTask(new RegexValidationTask());
    }

    @Override
    public GenericTask getTask(String name) {
        return taskMap.get(name);
    }
}
