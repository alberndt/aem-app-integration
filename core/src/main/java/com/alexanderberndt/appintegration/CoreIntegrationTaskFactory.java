package com.alexanderberndt.appintegration;

import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationTask;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationTaskFactory;
import com.alexanderberndt.appintegration.tasks.ExtractStaticResourcesTask;
import com.alexanderberndt.appintegration.tasks.RegexValidator;

public class CoreIntegrationTaskFactory implements IntegrationTaskFactory {

    @Override
    public IntegrationTask createTask(String taskName) {

        switch (taskName) {

            case RegexValidator.TASK_NAME:
                return new RegexValidator();
            case ExtractStaticResourcesTask.TASK_NAME:
                return new ExtractStaticResourcesTask();

            default:
                throw new AppIntegrationException("Task " + taskName + " is undefined");
        }
    }
}
