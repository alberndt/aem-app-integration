package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

public class ResourceLogger extends AbstractLogger {

    private String name;

    public ResourceLogger(@Nonnull LogAppender appender, @Nonnull String url) {
        super(appender);
        this.appender.appendLogger(this);
        this.setUrl(url);
    }

    public ResourceLogger(@Nonnull IntegrationLogger parentLogger, @Nonnull String url) {
        super(parentLogger);
        appender.appendLogger(this);
        this.setUrl(url);
    }

    public ResourceLogger(@Nonnull LogAppender appender, @Nonnull ExternalResourceRef resourceRef) {
        this(appender, resourceRef.getUri().toString());
    }

    public ResourceLogger(@Nonnull IntegrationLogger parentLogger, @Nonnull ExternalResourceRef resourceRef) {
        this(parentLogger, resourceRef.getUri().toString());
    }

    @Nonnull
    @Override
    public String getType() {
        return "resource";
    }

    @Nonnull
    @Override
    public String getLoggerName() {
        if (StringUtils.isNotBlank(this.name)) {
            return this.name;
        } else {
            return "xy" + super.getLoggerName();
        }
    }

    @Nonnull
    public TaskLogger createTaskLogger(String taskId, String taskName) {
        return new TaskLogger(this, taskId, taskName);
    }

    private void setUrl(String url) {
        setProperty("url", url);
        setLoggerInstanceName(url);
        try {
            final URL urlObj = new URL(url);
            final String fullPath = urlObj.getPath();
            this.name = StringUtils.substringAfterLast(fullPath, "/");
            setProperty("name", this.name);
            setProperty("path", urlObj.getHost() + StringUtils.substringBeforeLast(fullPath, "/"));
        } catch (MalformedURLException e) {
            this.name = url;
            setProperty("name", this.name);
            setProperty("path", null);
        }
    }

    public void setLoadStatus(String loadStatus) {
        setProperty("loadStatus", loadStatus);
    }

    public void setSize(String size) {
        this.setProperty("size", size);
    }

    public void setTime(String time) {
        this.setProperty("time", time);
    }

    public void setInitiator(String initiator) {
        this.setProperty("initiator", initiator);
    }

}
