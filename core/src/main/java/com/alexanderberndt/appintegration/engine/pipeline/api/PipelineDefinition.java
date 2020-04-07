package com.alexanderberndt.appintegration.engine.pipeline.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class PipelineDefinition {

    @JsonProperty
    private List<Step> steps;

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }


    public static class Step {

        @JsonProperty
        private String name;

        @JsonProperty
        private String filter;

        @JsonProperty
        private String fileTypes;

        @JsonProperty
        private Map<String, Object> configuration;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getFileTypes() {
            return fileTypes;
        }

        public void setFileTypes(String fileTypes) {
            this.fileTypes = fileTypes;
        }

        public Map<String, Object> getConfiguration() {
            return configuration;
        }

        public void setConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration;
        }
    }
}
