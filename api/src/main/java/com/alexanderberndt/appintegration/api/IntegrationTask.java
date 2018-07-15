package com.alexanderberndt.appintegration.api;

import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;

import java.util.Properties;

public interface IntegrationTask<IN, OUT> {

    void setup(final IntegrationTaskDef taskDef);

    OUT filter(IN data, IntegrationContext context);

    Class<IN> getInputClass();

    Class<OUT> getOutputClass();

}
