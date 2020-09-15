package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestAppInstance implements ApplicationInstance {

    private final String applicationId;

    private final String componentId;

    private final Map<String, String> contextMap = new HashMap<>();

    public TestAppInstance(String applicationId, String componentId) {
        this.applicationId = applicationId;
        this.componentId = componentId;
    }

    public TestAppInstance(String applicationId, String componentId, Map<String, String> contextMap) {
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
