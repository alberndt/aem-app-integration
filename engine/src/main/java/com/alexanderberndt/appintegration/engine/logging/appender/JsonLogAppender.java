package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class JsonLogAppender implements IntegrationLogAppender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Nonnull
    private final WriterSupplier writerSupplier;

    private final List<JsonLogEntry> rootLoggersList = new ArrayList<>();

    private final Map<AbstractLogger, JsonLogEntry> loggerJsonLoggerMap = new WeakHashMap<>();

    private AbstractLogger lastLogger;

    private JsonLogEntry lastLogEntry;

    public JsonLogAppender(@Nonnull WriterSupplier writerSupplier) {
        LOG.info("New JsonLogAppender created...");
        this.writerSupplier = writerSupplier;
    }

    @Override
    public void close() throws IOException {
        LOG.info("close()");
        try (final Writer writer = writerSupplier.createWriter()) {
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(writer, rootLoggersList);
        }
    }

    private JsonLogEntry getJsonLogEntry(@Nonnull AbstractLogger logger) {
        if (logger == lastLogger) {
            return lastLogEntry;
        } else {
            final JsonLogEntry logEntry = loggerJsonLoggerMap.get(logger);
            if (logEntry != null) {
                lastLogger = logger;
                lastLogEntry = logEntry;
            }
            return logEntry;
        }
    }


    @Override
    public synchronized void appendLogger(@Nonnull AbstractLogger logger) {
        if (!loggerJsonLoggerMap.containsKey(logger)) {
            LOG.debug("appendLogger({})", logger.getLoggerName());
            final JsonLogEntry jsonLogEntry = new JsonLogEntry(logger.getType());
            loggerJsonLoggerMap.put(logger, jsonLogEntry);
            final AbstractLogger parentLogger = logger.getParentLogger();
            if (parentLogger == null) {
                rootLoggersList.add(jsonLogEntry);
            } else {
                final JsonLogEntry parentEntry = getJsonLogEntry(parentLogger);
                if (parentEntry != null) {
                    parentEntry.addSubEntry(jsonLogEntry);
                } else {
                    LOG.warn("Parent-Logger {} of {} was not appended before! Logger will be ignored in output!",
                            parentLogger.getLoggerName(), logger.getLoggerName());
                }
            }
            lastLogger = logger;
            lastLogEntry = jsonLogEntry;
        } else {
            LOG.warn("appendLogger({}) failed, logger was already appended!", logger.getLoggerName());
        }
    }

    @Override
    public synchronized void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        LOG.debug("setLoggerSummary({}, {}, {})", logger.getLoggerName(), status, message);
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setSummary(status, message);
        } else {
            LOG.error("Can't write summery for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    @Override
    public void setLoggerStatus(@Nonnull AbstractLogger logger, LogStatus status) {
        LOG.debug("setLoggerStatus({}, {})", logger.getLoggerName(), status);
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setStatus(status);
        } else {
            LOG.error("Can't set status for unknown logger (logger: {}, status: {})", logger, status);
        }
    }

    @Override
    public synchronized void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value) {
        LOG.debug("setLoggerProperty({}, {} = {})", logger.getLoggerName(), key, value);
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setProperty(key, value);
        } else {
            LOG.error("Can't set logger property for unknown logger (logger: {}, key: {}, value: {})", logger, key, value);
        }
    }

    @Override
    public synchronized void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        LOG.debug("appendLogEntry({}, {}, {})", logger.getLoggerName(), status, message);
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.addSubEntry(new JsonLogEntry(logger.getType(), status, message));
        } else {
            LOG.error("Can't append log-entry for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    public interface WriterSupplier {
        Writer createWriter() throws IOException;
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({"type", "status", "message", "properties", "entries"})
    private static class JsonLogEntry {

        @JsonProperty
        private final String type;

        @JsonProperty
        private LogStatus status;

        @JsonProperty
        private String message;

        @JsonProperty("properties")
        private Map<String, String> propertyMap;

        @JsonProperty("entries")
        private List<JsonLogEntry> subEntries;

        public JsonLogEntry(String type) {
            this.type = type;
        }

        public JsonLogEntry(String type, LogStatus status, String message) {
            this(type);
            setSummary(status, message);
        }

        public void setSummary(LogStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public void setStatus(LogStatus status) {
            this.status = status;
        }

        public void setProperty(String key, String value) {
            if (this.propertyMap == null) {
                this.propertyMap = new HashMap<>();
            }
            this.propertyMap.put(key, value);
        }

        public void addSubEntry(@Nonnull JsonLogEntry subEntry) {
            if (this.subEntries == null) {
                this.subEntries = new ArrayList<>();
            }
            this.subEntries.add(subEntry);
        }
    }
}
