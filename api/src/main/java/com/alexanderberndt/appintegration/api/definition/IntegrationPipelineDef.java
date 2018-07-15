package com.alexanderberndt.appintegration.api.definition;

import java.util.List;
import java.util.Properties;

public class IntegrationPipelineDef {

    private Properties properties;

    private List<IntegrationTaskDef> subtasks;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<IntegrationTaskDef> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<IntegrationTaskDef> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "IntegrationPipelineDef{\n" +
                "properties=" + properties +
                ",\n subtasks=" + subtasks +
                "\n}";
    }
}
