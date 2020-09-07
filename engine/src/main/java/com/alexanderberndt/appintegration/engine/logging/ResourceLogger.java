package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

public class ResourceLogger extends AbstractLogger {

    public ResourceLogger(@Nonnull LogAppender appender, @Nonnull String url) {
        super(appender);
        this.setUrl(url);
    }

    public ResourceLogger(@Nonnull IntegrationLogger parentLogger, @Nonnull String url) {
        super(parentLogger);
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
    public TaskLogger createTaskLogger(GenericTask task, String taskNamespace) {
        return new TaskLogger(this, task, taskNamespace);
    }

    @Nonnull
    public TaskLogger createTaskLogger(String taskName, String taskNamespace) {
        return new TaskLogger(this, taskName, taskNamespace);
    }

    public void setUrl(String url) {
        setProperty("url", url);
        setLoggerInstanceName(url);
        try {
            final URL urlObj = new URL(url);
            final String fullPath = urlObj.getPath();
            setProperty("name", StringUtils.substringAfterLast(fullPath, "/"));
            setProperty("path", urlObj.getHost() + StringUtils.substringBeforeLast(fullPath, "/"));
        } catch (MalformedURLException e) {
            setProperty("name", url);
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
