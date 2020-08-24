package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class ConvertibleReaderValue extends AbstractConvertibleValue<Reader> {

    public ConvertibleReaderValue(Reader value, Charset charset) {
        super(value, charset);
    }

    @Nonnull
    @Override
    public ConvertibleValue<Reader> convertToReaderValue() {
        return this;
    }

    @Nonnull
    @Override
    public ConvertibleValue<byte[]> convertToByteArrayValue() throws IOException {
        return convertToInputStreamValue().convertToByteArrayValue();
    }

    @Nullable
    @Override
    protected InputStream convertToInputStream() {
        final Reader reader = this.get();
        return (reader != null) ? new ReaderInputStream(this.get(), this.getCharset()) : null;
    }

    @Nullable
    @Override
    protected String convertToString() throws IOException {
        final Reader reader = this.get();
        if (reader != null) {
            final StringWriter temp = new StringWriter();
            IOUtils.copy(reader, temp);
            return temp.toString();
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    public ConvertibleValue<Reader> changeCharset(@Nonnull Charset charset) {
        return new ConvertibleReaderValue(this.get(), charset);
    }
}
