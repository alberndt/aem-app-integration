package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;

public class Slf4jLogAppender implements IntegrationLogAppender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void appendLogger(@Nonnull AbstractLogger logger) {
        LOG.info("append {}", logger.getLoggerName());
    }

    @Override
    public void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        getLogMethod(status).log("set summary {}: {} - {}", logger.getLoggerName(), status, message);
    }

    @Override
    public void setLoggerStatus(@Nonnull AbstractLogger logger, LogStatus status) {
        getLogMethod(status).log("set status for {}: {}", logger.getLoggerName(), status);
    }

    @Override
    public void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value) {
        LOG.info("set property for {}: {} = {}", logger.getLoggerName(), key, value);
    }

    @Override
    public void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        getLogMethod(status).log("append entry for {}: {} - {}", logger.getLoggerName(), status, message);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Nonnull
    private LogMethod getLogMethod(LogStatus status) {
        switch ((status != null) ? status : LogStatus.INFO) {
            case ERROR:
            case FAILED:
                return LOG::error;
            case WARNING:
                return LOG::warn;
            case INFO:
            default:
                return LOG::info;
        }
    }

    private interface LogMethod {
        void log(String message, Object... args);
    }
}
