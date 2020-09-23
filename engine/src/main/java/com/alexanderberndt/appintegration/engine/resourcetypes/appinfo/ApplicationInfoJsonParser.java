package com.alexanderberndt.appintegration.engine.resourcetypes.appinfo;

import com.alexanderberndt.appintegration.engine.resources.conversion.ConversionException;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

@Component
public class ApplicationInfoJsonParser implements TextParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Nonnull
    @Override
    public Class<?> getTargetType() {
        return ApplicationInfoJson.class;
    }

    @Override
    public Object parse(@Nonnull Reader reader) throws IOException {
        try {
            return objectMapper.readerFor(ApplicationInfoJson.class).readValue(reader);
        } catch (JsonProcessingException e) {
            throw new ConversionException(String.format("Cannot parse application-info.json, due to: %s", e.getMessage()), e);
        }
    }

    @Override
    public String serialize(@Nonnull Object source) throws IOException {
        return objectMapper.writerFor(ApplicationInfoJson.class).writeValueAsString(source);
    }

}
