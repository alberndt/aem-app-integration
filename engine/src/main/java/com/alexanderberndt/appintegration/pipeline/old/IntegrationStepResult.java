package com.alexanderberndt.appintegration.pipeline.old;

@Deprecated
// ToDo: Integrate in ProcessingStep
public class IntegrationStepResult<T> {

    public enum Status {NOTHING_DONE, SKIPPED, OK, WARNING, ERROR}

    private Status status;

    private T result;


    private void mergeStatus(Status newStatus) {
        if ((this.status == null) || (this.status.ordinal() < newStatus.ordinal())) {
            this.status = newStatus;
        }
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

}
