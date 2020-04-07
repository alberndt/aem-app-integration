package com.alexanderberndt.appintegration.engine.pipeline.api;

import java.util.ArrayList;
import java.util.List;

public class IntegrationStepResult<T> {

    public enum Status {NOTHING_DONE, SKIPPED, OK, WARNING, ERROR}

    private Status status;

    private T result;

    private final List<IntegrationMessage> messageList = new ArrayList<>();

    
    private void mergeStatus(Status newStatus) {
        if ((this.status == null) || (this.status.ordinal() < newStatus.ordinal())) {
            this.status = newStatus;
        }
    }

    public void addInfo(String messagePattern, Object... objects) {
        messageList.add(IntegrationMessage.createMessage(IntegrationMessage.Type.INFO, messagePattern, objects));
    }

    public void addWarning(String messagePattern, Object... objects) {
        mergeStatus(Status.WARNING);
        messageList.add(IntegrationMessage.createMessage(IntegrationMessage.Type.WARNING, messagePattern, objects));
    }

    public void addError(String messagePattern, Object... objects) {
        mergeStatus(Status.ERROR);
        messageList.add(IntegrationMessage.createMessage(IntegrationMessage.Type.ERROR, messagePattern, objects));
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<IntegrationMessage> getMessageList() {
        return messageList;
    }

}
