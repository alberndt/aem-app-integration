package com.alexanderberndt.appintegration.engine.resourcetypes.appinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ApplicationInfoJson {

    @JsonProperty
    private String name;

    @JsonProperty
    private String version;

    @JsonProperty(required = true)
    private Map<String, ComponentInfoJson> components;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, ComponentInfoJson> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ComponentInfoJson> components) {
        this.components = components;
    }
}
