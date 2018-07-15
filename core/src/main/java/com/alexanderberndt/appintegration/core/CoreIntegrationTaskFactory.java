package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.api.IntegrationTask;
import com.alexanderberndt.appintegration.api.IntegrationTaskFactory;
import com.alexanderberndt.appintegration.core.impl.HttpDownloadTask;
import com.alexanderberndt.appintegration.core.impl.RegexValidator;

public class CoreIntegrationTaskFactory implements IntegrationTaskFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <IN> IntegrationTask<IN, ?> createTask(String taskName, Class<IN> inputClass) {
        switch (taskName) {
            case RegexValidator.TASK_NAME:
                if (inputClass.isAssignableFrom(String.class)) {
                    return (IntegrationTask<IN, ?>) new RegexValidator();
                }

            case HttpDownloadTask.TASK_NAME:
                return (IntegrationTask<IN, ?>) new HttpDownloadTask();

            default:
                return null;

        }
    }
}
