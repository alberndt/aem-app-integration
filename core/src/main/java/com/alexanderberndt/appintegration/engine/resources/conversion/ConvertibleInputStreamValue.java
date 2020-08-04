package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class ConvertibleInputStreamValue extends AbstractConvertibleValue<InputStream> {

    public ConvertibleInputStreamValue(InputStream value, Charset charset) {
        super(value, charset);
    }

    @Nonnull
    @Override
    public ConvertibleValue<InputStream> convertToInputStreamValue() {
        return this;
    }

    @Nonnull
    @Override
    public ConvertibleValue<String> convertToStringValue() throws IOException {
        return this.convertToReaderValue().convertToStringValue();
    }

    @Nullable
    @Override
    protected Reader convertToReader() {
        final InputStream inputStream = this.get();
        return (inputStream != null) ? new InputStreamReader(inputStream, this.getCharset()) : null;
    }

    @Nullable
    @Override
    protected byte[] convertToByteArray() throws IOException {
        final InputStream inputStream = this.get();
        return (inputStream != null) ? IOUtils.toByteArray(inputStream) : null;
    }

    @Nonnull
    @Override
    public ConvertibleValue<InputStream> changeCharset(@Nonnull Charset charset) {
        return new ConvertibleInputStreamValue(this.get(), charset);
    }

}
