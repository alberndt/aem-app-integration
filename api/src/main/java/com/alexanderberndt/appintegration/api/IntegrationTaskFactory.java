package com.alexanderberndt.appintegration.api;

public interface IntegrationTaskFactory {

    <IN> IntegrationTask<IN, ?> createTask(String taskName, Class<IN> inputClass);

}
