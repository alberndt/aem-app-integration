package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

@Component(service = TextParser.class)
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
    protected String serializeType(@Nonnull String source) {
        return source;
    }

}
