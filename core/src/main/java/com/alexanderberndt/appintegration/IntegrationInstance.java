package com.alexanderberndt.appintegration;

import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationJob;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntegrationInstance {

    private final IntegrationJob integrationJob;

    private final Map<String, Object> instanceProperties = new LinkedHashMap<>();

    public IntegrationInstance(IntegrationJobImpl integrationJob, Map<String, Object> instanceProperties) {
        this.integrationJob = integrationJob;
        if (instanceProperties != null) {
            this.instanceProperties.putAll(instanceProperties);
        }
    }

    public IntegrationJob getIntegrationJob() {
        return integrationJob;
    }

    public Map<String, Object> getInstanceProperties() {
        return instanceProperties;
    }
}
