package com.alexanderberndt.appintegration.api.definition;


import java.util.List;
import java.util.Properties;

public class IntegrationTaskDef {

    private String task;

    private Properties properties;

    private List<IntegrationTaskDef> subtasks;

    public IntegrationTaskDef() {
    }

    public IntegrationTaskDef(String task, Properties properties, List<IntegrationTaskDef> subtasks) {
        this.task = task;
        this.properties = properties;
        this.subtasks = subtasks;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

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
        return "IntegrationTaskDef{\n" +
                "  task='" + task + '\'' +
                ",\n   properties=" + properties +
                ",\n   subtasks=" + subtasks +
                "\n}";
    }
}
