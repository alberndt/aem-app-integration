package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.api.IntegrationTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JsonTaskPipelineParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTaskPipelineParser.class);

    private JsonTaskPipelineParser() {
    }

    public static List<IntegrationTask<?, ?>> parseTaskPipelineJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            if (jsonNode.isArray()) {

            }


        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;

    }

}
