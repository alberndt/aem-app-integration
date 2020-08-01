package com.alexanderberndt.appintegration.pipeline.context;

public interface Context {

    public enum Ranking {GLOBAL, PIPELINE_EXECUTION, PIPELINE_DEFINITION, TASK_DEFAULT, NO_RANK}

    String getNamespace();

    Ranking getRank();

    void addWarning(String message);

    void addError(String message);

}
