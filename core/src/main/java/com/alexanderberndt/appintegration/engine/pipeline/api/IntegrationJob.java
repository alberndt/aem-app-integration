package com.alexanderberndt.appintegration.engine.pipeline.api;

import org.slf4j.Logger;

public interface IntegrationJob {

    void executeImport();

    void addWarning(Logger logger, String messagePattern, Object... objects);

    void addWarning(String messagePattern, Object... objects);

    void addWarning(String text);

    void addError(String messagePattern, Object... objects);

    void addError(Logger logger, String messagePattern, Object... objects);
}
