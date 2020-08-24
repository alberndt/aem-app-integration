package com.alexanderberndt.appintegration.engine.resourcetypes.appinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentInfoJson {

    @JsonProperty
    private String name;

    @JsonProperty(required = true)
    private String url;

    @JsonProperty
    private String dialog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }
}
