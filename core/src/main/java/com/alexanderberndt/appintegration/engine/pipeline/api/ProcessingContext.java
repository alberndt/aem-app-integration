package com.alexanderberndt.appintegration.engine.pipeline.api;

import java.util.Map;

public class ProcessingContext {

    private Map<String, Object> pipelineFilterConfiguration;

    public Map<String, Object> getPipelineFilterConfiguration() {
        return pipelineFilterConfiguration;
    }

    public void setPipelineFilterConfiguration(Map<String, Object> pipelineFilterConfiguration) {
        this.pipelineFilterConfiguration = pipelineFilterConfiguration;
    }
}
