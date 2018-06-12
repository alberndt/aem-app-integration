package com.alexanderberndt.aemappintegration.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface IntegrationStep<C, IN, OUT> {

    C convertConfig(JsonNode json, ObjectMapper objectMapper);

    OUT execute(IN input, C config, IntegrationContext context);

    Class<OUT> getOutputType();

    Class<IN> getInputType();

}
