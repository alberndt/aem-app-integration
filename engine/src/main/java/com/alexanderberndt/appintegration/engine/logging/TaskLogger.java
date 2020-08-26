package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class TaskLogger extends AbstractLogger {

    private String parentInstanceName;

    public TaskLogger(@Nonnull AbstractLogger parentLogger, @Nonnull GenericTask task, @Nonnull String taskNamespace) {
        super(parentLogger);
        this.parentInstanceName = parentLogger.loggerInstanceName;
        initProperties(task, taskNamespace);
    }

    public TaskLogger(@Nonnull AbstractLogger parentLogger, @Nonnull String taskName, @Nonnull String taskNamespace) {
        super(parentLogger);
        this.parentInstanceName = parentLogger.loggerInstanceName;
        initProperties(taskName, taskNamespace);
    }


    public TaskLogger(@Nonnull IntegrationLogAppender appender, @Nonnull GenericTask task, @Nonnull String taskNamespace) {
        super(appender);
        initProperties(task, taskNamespace);
    }

    public TaskLogger(@Nonnull IntegrationLogAppender appender, @Nonnull String taskName, @Nonnull String taskNamespace) {
        super(appender);
        initProperties(taskName, taskNamespace);
    }

    @Nonnull
    @Override
    public String getType() {
        return "task";
    }

    private void initProperties(@Nonnull GenericTask task, @Nonnull String taskNamespace) {
        initProperties(task.getName(), taskNamespace);
        setProperty("humanReadableTaskName", task.getHumanReadableName());
    }

    private void initProperties(@Nonnull String taskName, @Nonnull String taskNamespace) {
        if (StringUtils.equals(taskName, taskNamespace)) {
            setProperty("taskName", taskNamespace);
        } else {
            setProperty("taskName", String.format("%s (%s)", taskNamespace, taskName));
        }

        if (StringUtils.isNotBlank(parentInstanceName)) {
            setLoggerInstanceName(taskNamespace + " @ " + parentInstanceName);
        } else {
            setLoggerInstanceName(taskNamespace);
        }
    }
}
