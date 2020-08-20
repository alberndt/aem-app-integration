package com.alexanderberndt.appintegration.engine.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

import static com.alexanderberndt.appintegration.engine.logging.LogStatus.INFO;
import static com.alexanderberndt.appintegration.engine.logging.LogStatus.WARNING;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = LogEntry.class, name = "message"),
        @Type(value = IntegrationLog.class, name = "integration"),
        @Type(value = ResourceLog.class, name = "resource"),
        @Type(value = TaskLog.class, name = "task"),
        @Type(value = MessageEntry.class, name = "message2")
})
public
class LogEntry {

    @JsonProperty
    private LogStatus status = INFO;

    @JsonProperty
    private String msg;

    @JsonProperty("entries")
    private List<LogEntry> logEntries;


    public void addWarning(@Nonnull String message, Object... args) {
        LogEntry entry = new LogEntry();
        entry.setStatus(WARNING);
        entry.setMsg(message, args);
        addEntry(entry);
    }


    protected <T extends LogEntry> T addEntry(@Nonnull final T entry) {
        if (this.logEntries == null) this.logEntries = new ArrayList<>();
        this.logEntries.add(entry);
        return entry;
    }

    public void setMsg(@Nonnull String message, Object... args) {
        try {
            this.msg = String.format(message, args);
        } catch (IllegalFormatException e) {
            this.msg = message + " " + Arrays.toString(args);
        }
    }

    public MessageEntry addSubMessage(@Nonnull final LogStatus status, @Nonnull String message, Object... args) {
        MessageEntry newEntry = new MessageEntry();
        newEntry.setStatus(status);
        newEntry.setMsg(message, args);
        addEntry(newEntry);
        return newEntry;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }


}
