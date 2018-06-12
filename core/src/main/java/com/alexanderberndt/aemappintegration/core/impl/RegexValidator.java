package com.alexanderberndt.aemappintegration.core.impl;

import com.alexanderberndt.aemappintegration.api.IntegrationContext;
import com.alexanderberndt.aemappintegration.api.IntegrationStep;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegexValidator implements IntegrationStep<RegexValidator.Config, String, String> {


//    @Override
//    public void validate(String data, ) {
//        if (config == null) {
//            throw new RuntimeException("missing config");
//        }
//
//        if (!"Hello World".equals(config.getRegex())) {
//            throw new RuntimeException("wrong config");
//        }
//
//        System.out.println(data);
//        System.out.println(config.getRegex());
//
//    }

    @Override
    public String execute(String input, Config config, IntegrationContext context) {
        if (config.getRegex().equals(input)) {
            return null;
        } else {
            return input;
        }
    }

    @Override
    public Config convertConfig(JsonNode json, ObjectMapper objectMapper) {
        return objectMapper.convertValue(json, Config.class);
    }

    @Override
    public Class<String> getOutputType() {
        return String.class;
    }

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    public static class Config {

        private String regex;

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }
}
