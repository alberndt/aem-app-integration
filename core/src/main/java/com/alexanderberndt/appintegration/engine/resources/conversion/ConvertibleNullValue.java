package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class ConvertibleNullValue extends AbstractConvertibleValue<Object> {

    public ConvertibleNullValue(@Nonnull Charset charset) {
        super(null, charset);
    }

    @Nullable
    @Override
    protected InputStream convertToInputStream() {
        return null;
    }

    @Nullable
    @Override
    protected Reader convertToReader() {
        return null;
    }

    @Nullable
    @Override
    protected byte[] convertToByteArray() throws IOException {
        return null;
    }

    @Nullable
    @Override
    protected String convertToString() throws IOException {
        return null;
    }

    @Nonnull
    @Override
    public ConvertibleNullValue changeCharset(@Nonnull Charset charset) {
        return new ConvertibleNullValue(charset);
    }
}
