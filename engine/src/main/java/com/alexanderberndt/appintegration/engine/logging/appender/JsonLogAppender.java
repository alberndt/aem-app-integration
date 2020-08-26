package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        this.writerSupplier = writerSupplier;
    }

    @Override
    public void close() throws IOException {
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
            final JsonLogEntry jsonLogEntry = new JsonLogEntry();
            loggerJsonLoggerMap.put(logger, jsonLogEntry);
            if (logger.getParentLogger() == null) {
                rootLoggersList.add(jsonLogEntry);
            }
            lastLogger = logger;
            lastLogEntry = jsonLogEntry;
        }
    }

    @Override
    public synchronized void setLoggerSummary(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setSummary(status, message);
        } else {
            LOG.error("Can't write summery for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    @Override
    public void setLoggerStatus(@Nonnull AbstractLogger logger, LogStatus status) {
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setStatus(status);
        } else {
            LOG.error("Can't set status for unknown logger (logger: {}, status: {})", logger, status);
        }
    }

    @Override
    public synchronized void setLoggerProperty(@Nonnull AbstractLogger logger, @Nonnull String key, String value) {
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.setProperty(key, value);
        } else {
            LOG.error("Can't set logger property for unknown logger (logger: {}, key: {}, value: {})", logger, key, value);
        }
    }

    @Override
    public synchronized void appendLogEntry(@Nonnull AbstractLogger logger, LogStatus status, String message) {
        final JsonLogEntry logEntry = getJsonLogEntry(logger);
        if (logEntry != null) {
            logEntry.addSubEntry(new JsonLogEntry(status, message));
        } else {
            LOG.error("Can't append log-entry for unknown logger (logger: {}, status: {}, message: {})", logger, status, message);
        }
    }

    public interface WriterSupplier {
        Writer createWriter() throws IOException;
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private static class JsonLogEntry {

        @JsonProperty
        private LogStatus status;

        @JsonProperty
        private String message;

        @JsonProperty
        private Map<String, String> propertyMap;

        @JsonProperty
        private List<JsonLogEntry> subEntries;

        public JsonLogEntry() {
        }

        public JsonLogEntry(LogStatus status, String message) {
            this.status = status;
            this.message = message;
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
