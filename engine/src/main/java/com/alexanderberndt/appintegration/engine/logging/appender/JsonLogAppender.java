package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class JsonLogAppender extends AbstractLogAppender<JsonLogAppender.JsonLogEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Nonnull
    private final WriterSupplier writerSupplier;

    public JsonLogAppender(@Nonnull WriterSupplier writerSupplier) {
        LOG.info("New JsonLogAppender created...");
        this.writerSupplier = writerSupplier;
    }

    @Nonnull
    @Override
    protected JsonLogEntry createNewLogEntry(@Nullable JsonLogEntry parentEntry, @Nonnull AbstractLogger logger) {
        final JsonLogEntry newLogEntry = new JsonLogEntry(logger.getType());
        if (parentEntry != null) {
            parentEntry.registerSubEntry(newLogEntry);
        }
        return newLogEntry;
    }

    @Override
    protected void createNewLogMessage(@Nullable JsonLogEntry parentEntry, @Nullable LogStatus status, @Nullable String message) {
        final JsonLogEntry newLogEntry = new JsonLogEntry("message", status, message);
        if (parentEntry != null) {
            parentEntry.registerSubEntry(newLogEntry);
        }
    }

    @Override
    public void close() throws IOException {
        LOG.info("close()");
        try (final Writer writer = writerSupplier.createWriter()) {
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(writer, rootLoggersList);
        }
    }

    public interface WriterSupplier {
        Writer createWriter() throws IOException;
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({"type", "status", "message", "properties", "entries"})
    protected static class JsonLogEntry implements AbstractLogAppender.LogEntry<JsonLogEntry> {

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

        public void registerSubEntry(@Nonnull JsonLogEntry subEntry) {
            if (this.subEntries == null) {
                this.subEntries = new ArrayList<>();
            }
            this.subEntries.add(subEntry);
        }
    }
}
