package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public class StringConverter extends AbstractTextParser<String> {

    public StringConverter() {
        super(String.class);
    }

    @Override
    public String parse(@Nonnull Reader reader) throws IOException {
        final StringWriter temp = new StringWriter();
        IOUtils.copy(reader, temp);
        return temp.toString();
    }

    @Override
    protected String serializeType(@Nonnull String source) throws IOException {
        return source;
    }

}
