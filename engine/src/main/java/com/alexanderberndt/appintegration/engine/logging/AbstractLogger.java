package com.alexanderberndt.appintegration.engine.logging;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.IllegalFormatException;

import static com.alexanderberndt.appintegration.engine.logging.LogStatus.*;


public abstract class AbstractLogger {

    @Nullable
    protected final AbstractLogger parentLogger;

    @Nonnull
    protected final IntegrationLogAppender appender;

    @Nullable
    protected String loggerInstanceName;

    private AbstractLogger(@Nullable AbstractLogger parentLogger, @Nonnull IntegrationLogAppender appender) {
        this.parentLogger = parentLogger;
        this.appender = appender;
        this.appender.appendLogger(this);
    }

    protected AbstractLogger(@Nonnull AbstractLogger parentLogger) {
        this(parentLogger, parentLogger.appender);
    }


    protected AbstractLogger(@Nonnull IntegrationLogAppender appender) {
        this(null, appender);
    }

    @Nonnull
    public abstract String getType();

    @Nonnull
    public String getLoggerName() {
        if (StringUtils.isNotBlank(loggerInstanceName)) {
            return String.format("%s logger (%s)", getType(), loggerInstanceName);
        } else {
            return String.format("%s logger", getType());
        }
    }

    protected void setLoggerInstanceName(@Nullable String loggerInstanceName) {
        this.loggerInstanceName = loggerInstanceName;
    }

    public void addInfo(@Nonnull String message, Object... args) {
        appender.appendLogEntry(this, INFO, format(message, args));
    }

    public void addWarning(@Nonnull String message, Object... args) {
        appender.appendLogEntry(this, WARNING, format(message, args));
    }

    public void addError(@Nonnull String message, Object... args) {
        appender.appendLogEntry(this, ERROR, format(message, args));
    }

    public void setSummary(@Nonnull LogStatus status, @Nonnull String message, Object... args) {
        appender.setLoggerSummary(this, status, format(message, args));
    }

    public void setStatus(@Nonnull LogStatus status) {
        appender.setLoggerStatus(this, status);
    }

    public void setProperty(@Nonnull String key, @Nullable String value) {
        appender.setLoggerProperty(this, key, value);
    }

    public DetailsLogger createDetailsLogger(String summery, Object... args) {
        final DetailsLogger detailsLogger = new DetailsLogger(this);
        detailsLogger.setSummary(INFO, summery, args);
        return detailsLogger;
    }

    protected static String format(@Nonnull String message, Object... args) {
        try {
            return String.format(message, args);
        } catch (IllegalFormatException e) {
            return message + " " + Arrays.toString(args);
        }
    }

    public AbstractLogger getParentLogger() {
        return parentLogger;
    }


    //    public MessageEntry addSubMessage(@Nonnull final LogStatus status, @Nonnull String message, Object... args) {
//        MessageEntry newEntry = new MessageEntry();
//        newEntry.setSummaryStatus(status);
//        newEntry.setSummary(message, args);
//        addEntry(newEntry);
//        return newEntry;
//    }
}
