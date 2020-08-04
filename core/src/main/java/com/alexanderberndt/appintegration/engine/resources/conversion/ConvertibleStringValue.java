package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

public class ConvertibleStringValue extends AbstractConvertibleValue<String> {

    public ConvertibleStringValue(String value, Charset charset) {
        super(value, charset);
    }

    @Nonnull
    @Override
    public ConvertibleValue<String> convertToStringValue() {
        return this;
    }

    @Nonnull
    @Override
    public ConvertibleValue<InputStream> convertToInputStreamValue() {
        return convertToReaderValue().convertToInputStreamValue();
    }


    @Nullable
    @Override
    protected Reader convertToReader() {
        final String str = this.get();
        return (str != null) ? new StringReader(str) : null;
    }

    @Nullable
    @Override
    protected byte[] convertToByteArray() {
        final String str = this.get();
        return (str != null) ? str.getBytes(this.getCharset()) : null;
    }


    @Nonnull
    @Override
    public ConvertibleValue<String> changeCharset(@Nonnull Charset charset) {
        return new ConvertibleStringValue(this.get(), charset);
    }
}
