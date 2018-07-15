package com.alexanderberndt.appintegration.api;

import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;

public interface IntegrationTask<IN, OUT> {

    OUT execute(IN data, IntegrationContext context);

    void setupTask(final IntegrationTaskDef taskDef);

    void tearDownTask();

    void beforeImportStarts();

    void afterImportFinished();

    Class<IN> getInputClass();

    Class<OUT> getOutputClass();

}
