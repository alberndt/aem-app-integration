package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public class StringConverter implements TextParser {

    @Nonnull
    @Override
    public Class<?> getTargetType() {
        return String.class;
    }

    @Override
    public String parse(@Nonnull Reader reader) throws IOException {
        final StringWriter temp = new StringWriter();
        IOUtils.copy(reader, temp);
        return temp.toString();
    }

    @Override
    public boolean isSerializeSupported() {
        return true;
    }

    @Override
    public String serialize(@Nonnull Object source) throws ConversionException {
        if (source instanceof String) {
            return (String) source;
        } else {
            throw new ConversionException("Object to serialize must instance of String, but is " + source);
        }

    }
}
