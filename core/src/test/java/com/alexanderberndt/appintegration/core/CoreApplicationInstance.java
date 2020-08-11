package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CoreApplicationInstance implements ApplicationInstance {

    private final String applicationId;

    private final String componentId;

    private final Map<String, String> contextMap = new HashMap<>();

    public CoreApplicationInstance(String applicationId, String componentId) {
        this.applicationId = applicationId;
        this.componentId = componentId;
    }

    public CoreApplicationInstance(String applicationId, String componentId, Map<String, String> contextMap) {
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.contextMap.putAll(contextMap);
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    public Map<String, String> getContextMap() {
        return Collections.unmodifiableMap(contextMap);
    }
}
