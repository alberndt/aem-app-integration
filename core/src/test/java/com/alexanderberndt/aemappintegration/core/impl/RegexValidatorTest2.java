package com.alexanderberndt.aemappintegration.core.impl;

import com.alexanderberndt.aemappintegration.api.IntegrationContext;
import com.alexanderberndt.aemappintegration.api.IntegrationStep;
import com.alexanderberndt.aemappintegration.core.IntegrationUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RegexValidatorTest2 {

    private static final String JSON1 = "{\"regex\":\"hello world\"}";

    @Test
    public void execute() throws IOException {
//
//        IntegrationContext context = new IntegrationContext() {
//        };
//
//        IntegrationStep validator = new RegexValidator();
//        //final JsonNode jsonNode = IntegrationUtil.getJsonFromResourceStream("regex-validator.json", this.getClass());
//        final JsonNode jsonNode = IntegrationUtil.getJson(JSON1);
//        Object config = validator.convertConfig(jsonNode, IntegrationUtil.createObjectMapper());
//
//
//        assertEquals("Hello", validator.execute("Hello", config, context));
//
//        assertEquals(null, validator.execute("hello world", config, context));
//
    }

    @Test
    public void convertConfig() {
    }

    @Test
    public void getOutputType() {
    }

    @Test
    public void getInputType() {
    }
}