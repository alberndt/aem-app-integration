package com.alexanderberndt.appintegration.pipeline.builder.definition;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unused")
public class TaskDefinition {

    @JsonProperty
    private String name;

    @JsonProperty
    private String filter;

    @JsonProperty
    private String fileTypes;

    @JsonProperty("configuration")
    private Map<String, Object> configurationGlobal = Collections.emptyMap();

    @JsonProperty("configuration.html")
    private Map<String, Object> configurationHtml = Collections.emptyMap();

    @JsonProperty("configuration.text")
    private Map<String, Object> configurationText = Collections.emptyMap();

    @JsonIgnore
    public Map<ExternalResourceType, Map<String, Object>> getFullConfiguration() {
        final EnumMap<ExternalResourceType, Map<String, Object>> configuration = new EnumMap<>(ExternalResourceType.class);
        configuration.put(ExternalResourceType.ANY, configurationGlobal);
        configuration.put(ExternalResourceType.TEXT, configurationText);
        configuration.put(ExternalResourceType.HTML, configurationHtml);
        return configuration;
    }

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

    public Map<String, Object> getConfigurationGlobal() {
        return configurationGlobal;
    }

    public void setConfigurationGlobal(Map<String, Object> configurationGlobal) {
        this.configurationGlobal = configurationGlobal;
    }

    public Map<String, Object> getConfigurationHtml() {
        return configurationHtml;
    }

    public void setConfigurationHtml(Map<String, Object> configurationHtml) {
        this.configurationHtml = configurationHtml;
    }

    public Map<String, Object> getConfigurationText() {
        return configurationText;
    }

    public void setConfigurationText(Map<String, Object> configurationText) {
        this.configurationText = configurationText;
    }
}
