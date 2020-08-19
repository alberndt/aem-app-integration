package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IntegrationLog extends LogEntry {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ResourceLog createResourceEntry(@Nonnull final ExternalResourceRef resourceRef) {
        return addEntry(new ResourceLog(resourceRef));
    }

    public void writeJson(@Nonnull final Writer writer) {
        try {
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(writer, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
