package com.alexanderberndt.appintegration.engine.logging;

import javax.annotation.Nonnull;
import java.io.Closeable;

public interface LogAppender extends Closeable {

    void appendLogger(@Nonnull AbstractLogger logger);

    void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message);

    void setLoggerStatus(@Nonnull AbstractLogger abstractLogger, LogStatus status);

    void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value);

    void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message);

}
