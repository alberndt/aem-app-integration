package com.alexanderberndt.appintegration.parser;

import java.util.List;
import java.util.Map;

class IntegrationTaskDef {

    private String task;

    private List<String> resourceTypes;

    private Map<String, Object> properties;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
