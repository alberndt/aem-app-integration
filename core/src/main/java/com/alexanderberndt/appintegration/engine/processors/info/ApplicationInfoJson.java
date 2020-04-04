package com.alexanderberndt.appintegration.engine.processors.info;

import com.alexanderberndt.appintegration.api.ApplicationInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

class ApplicationInfoJson implements ApplicationInfo {

    @JsonProperty
    private String name;

    @JsonProperty
    private String version;

    private Map<String, ComponentInfo> components;

    @JsonProperty("components")
    private void unpackComponents(Map<String, ComponentInfoJson> components) {
        this.components = new LinkedHashMap<>();
        this.components.putAll(components);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }


    @Override
    public Map<String, ComponentInfo> getComponents() {
        return components;
    }

    static class ComponentInfoJson implements ApplicationInfo.ComponentInfo {

        @JsonProperty
        private String name;

        @JsonProperty
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
}
