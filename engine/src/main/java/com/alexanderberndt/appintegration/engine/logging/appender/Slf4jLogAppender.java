package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.DetailsLogger;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public class Slf4jLogAppender implements LogAppender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Slf4jLogAppender() {
        // do not log this
    }

    @Override
    public void appendLogger(@Nonnull AbstractLogger logger) {
        // do not log this
    }

    @Override
    public void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        getLogMethod(logger, status).log("set summary {}: {} - {}", logger.getLoggerName(), status, message);
    }

    @Override
    public void setLoggerStatus(@Nonnull AbstractLogger logger, LogStatus status) {
        getLogMethod(logger, status).log("set status for {}: {}", logger.getLoggerName(), status);
    }

    @Override
    public void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value) {
        // do not log this
    }

    @Override
    public void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        getLogMethod(logger, status).log("append entry for {}: {} - {}", logger.getLoggerName(), status, message);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Nonnull
    private LogMethod getLogMethod(AbstractLogger logger, LogStatus status) {
        switch ((status != null) ? status : LogStatus.INFO) {
            case ERROR:
            case FAILED:
                return LOG::error;
            case WARNING:
                return LOG::warn;
            case INFO:
                return (logger instanceof DetailsLogger) ? LOG::debug : LOG::info;
            default:
                return LOG::info;
        }
    }

    private interface LogMethod {
        void log(String message, Object... args);
    }
}
