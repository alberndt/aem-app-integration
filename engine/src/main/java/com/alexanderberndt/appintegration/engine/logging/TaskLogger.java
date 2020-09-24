package com.alexanderberndt.appintegration.engine.logging;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class TaskLogger extends AbstractLogger {

    private final String taskId;

    private String parentInstanceName;

    public TaskLogger(@Nonnull AbstractLogger parentLogger, @Nonnull String taskId, @Nonnull String taskName) {
        super(parentLogger);
        this.taskId = taskId;
        this.parentInstanceName = parentLogger.loggerInstanceName;
        this.appender.appendLogger(this);
        initProperties(taskId, taskName);
    }

    public TaskLogger(@Nonnull LogAppender appender, @Nonnull String taskId, @Nonnull String taskName) {
        super(appender);
        this.taskId = taskId;
        this.appender.appendLogger(this);
        initProperties(taskId, taskName);
    }

    @Nonnull
    @Override
    public String getType() {
        return "task";
    }

    @Nonnull
    @Override
    public String getLoggerName() {
        return taskId;
    }

    private void initProperties(@Nonnull String taskId, @Nonnull String taskName) {
        if (StringUtils.equals(taskName, taskId)) {
            setProperty("taskName", taskId);
        } else {
            setProperty("taskName", String.format("%s (%s)", taskId, taskName));
        }

        if (StringUtils.isNotBlank(parentInstanceName)) {
            setLoggerInstanceName(taskId + " @ " + parentInstanceName);
        } else {
            setLoggerInstanceName(taskId);
        }
    }
}
