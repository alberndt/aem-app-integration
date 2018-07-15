package com.alexanderberndt.appintegration.core.impl;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class RegexValidatorTest {

    private static final String JSON1 = "{\"regex\":\"hello world\"}";




    @Test
    public void execute() throws IOException {

        RegexValidator validator = new RegexValidator();

        Properties props = new Properties();
        props.setProperty("regex", "hello");



//        validator.setup(null);
//        validator.filter("Here is my hello world!", null);


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