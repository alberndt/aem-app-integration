package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceLog extends LogEntry {

    @JsonProperty
    private String url;

    @JsonProperty
    private String name;

    @JsonProperty
    private String path;

    @JsonProperty
    private String size;

    @JsonProperty
    private String time;

    @JsonProperty
    private String initiator;

    public ResourceLog(ExternalResourceRef resourceRef) {
        this.setUrl(resourceRef.getUrl());
        try {
            final URL urlObj = new URL(resourceRef.getUrl());
            final String fullPath = urlObj.getPath();
            this.setName(StringUtils.substringAfterLast(fullPath, "/"));
            this.setPath(urlObj.getHost() + StringUtils.substringBeforeLast(fullPath, "/"));
        } catch (MalformedURLException e) {
            this.setName(resourceRef.getUrl());
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
}
