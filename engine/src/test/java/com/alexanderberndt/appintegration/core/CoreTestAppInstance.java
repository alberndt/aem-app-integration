package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CoreTestAppInstance implements ApplicationInstance {

    private final String applicationId;

    private final String componentId;

    private final Map<String, String> contextMap = new HashMap<>();

    public CoreTestAppInstance(String applicationId, String componentId) {
        this.applicationId = applicationId;
        this.componentId = componentId;
    }

    public CoreTestAppInstance(String applicationId, String componentId, Map<String, String> contextMap) {
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.contextMap.putAll(contextMap);
    }

    @Nonnull
    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Nonnull
    @Override
    public String getComponentId() {
        return componentId;
    }

    public Map<String, String> getContextMap() {
        return Collections.unmodifiableMap(contextMap);
    }
}
