package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class TaskLog extends LogEntry {

    @JsonProperty
    private final String taskName;

    @JsonProperty
    private final String humanReadableTaskName;

    public TaskLog(String taskName, String humanReadableTaskName) {
        this.taskName = taskName;
        this.humanReadableTaskName = humanReadableTaskName;
    }

    public TaskLog(GenericTask task, String taskNamespace) {
        if (StringUtils.equals(task.getName(), taskNamespace)) {
            this.taskName = taskNamespace;
        } else {
            this.taskName = String.format("%s (%s)", taskNamespace, task.getName());
        }

        this.humanReadableTaskName = task.getHumanReadableName();
    }

    public String getTaskName() {
        return taskName;
    }

    public String getHumanReadableTaskName() {
        return humanReadableTaskName;
    }
}
