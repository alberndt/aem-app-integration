package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.*;

public abstract class AbstractLogAppender<T extends AbstractLogAppender.LogEntry<T>> implements LogAppender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected final List<T> rootLoggersList = new ArrayList<>();

    private final Map<AbstractLogger, T> loggerToLogEntryMap = new WeakHashMap<>();

    private AbstractLogger lastLogger;

    private T lastLogEntry;

    @Nullable
    protected abstract T createNewLogEntry(@Nullable T parentEntry, @Nonnull AbstractLogger logger);

    protected abstract void createNewLogMessage(@Nullable T parentEntry, @Nullable LogStatus status, @Nullable String message);

    @Nullable
    private T getLogEntry(@Nonnull AbstractLogger logger) {
        if (logger == lastLogger) {
            return lastLogEntry;
        } else {
            final T logEntry = loggerToLogEntryMap.get(logger);
            if (logEntry != null) {
                lastLogger = logger;
                lastLogEntry = logEntry;
            }
            return logEntry;
        }
    }

    @Override
    public synchronized void appendLogger(@Nonnull AbstractLogger logger) {

        LOG.debug("appendLogger({})", logger.getLoggerName());

        if (loggerToLogEntryMap.containsKey(logger)) {
            LOG.warn("appendLogger({}) failed, logger was already appended!", logger.getLoggerName());
            return;
        }

        final T parentLogEntry = Optional.of(logger)
                .map(AbstractLogger::getParentLogger)
                .map(this::getLogEntry)
                .orElse(null);

        final T newLogEntry = this.createNewLogEntry(parentLogEntry, logger);
        if (newLogEntry != null) {
            loggerToLogEntryMap.put(logger, newLogEntry);
            if (parentLogEntry == null) {
                rootLoggersList.add(newLogEntry);
                if (logger.getParentLogger() != null) {
                    LOG.warn("Parent-Logger {} of {} was not appended before! Created new root-logger!",
                            logger.getParentLogger().getLoggerName(), logger.getLoggerName());
                }
            }

            lastLogger = logger;
            lastLogEntry = newLogEntry;
        }
    }

    @Override
    public synchronized void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        LOG.debug("setLoggerSummary({}, {}, {})", logger.getLoggerName(), status, message);
        final T logEntry = getLogEntry(logger);
        if (logEntry != null) {
            logEntry.setSummary(status, message);
        } else {
            LOG.error("Can't write summery for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    @Override
    public void setLoggerStatus(@Nonnull AbstractLogger logger, LogStatus status) {
        LOG.debug("setLoggerStatus({}, {})", logger.getLoggerName(), status);
        final T logEntry = getLogEntry(logger);
        if (logEntry != null) {
            logEntry.setStatus(status);
        } else {
            LOG.error("Can't set status for unknown logger (logger: {}, status: {})", logger, status);
        }
    }

    @Override
    public synchronized void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value) {
        LOG.debug("setLoggerProperty({}, {} = {})", logger.getLoggerName(), key, value);
        final T logEntry = getLogEntry(logger);
        if (logEntry != null) {
            logEntry.setProperty(key, value);
        } else {
            LOG.error("Can't set logger property for unknown logger (logger: {}, key: {}, value: {})", logger, key, value);
        }
    }

    @Override
    public synchronized void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        LOG.debug("appendLogEntry({}, {}, {})", logger.getLoggerName(), status, message);
        final T logEntry = getLogEntry(logger);
        if (logEntry != null) {
            createNewLogMessage(logEntry, status, message);
        } else {
            LOG.error("Can't append log-entry for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    public interface LogEntry<T extends LogEntry<T>> {

        void setSummary(LogStatus status, String message);

        void setStatus(LogStatus status);

        void setProperty(String key, String value);

    }

}
