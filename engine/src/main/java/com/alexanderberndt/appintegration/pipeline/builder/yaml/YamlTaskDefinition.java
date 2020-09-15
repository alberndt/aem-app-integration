package com.alexanderberndt.appintegration.pipeline.builder.yaml;

import com.alexanderberndt.appintegration.pipeline.builder.TaskDefinition;
import com.alexanderberndt.appintegration.utils.DataMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class YamlTaskDefinition implements TaskDefinition {

    @JsonProperty
    private String name;

    @JsonProperty
    private String filter;

    @JsonProperty
    private String fileTypes;

    @JsonProperty
    private DataMap configuration;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public String getFileTypes() {
        return fileTypes;
    }

    @Override
    public DataMap getConfiguration() {
        return configuration;
    }
}