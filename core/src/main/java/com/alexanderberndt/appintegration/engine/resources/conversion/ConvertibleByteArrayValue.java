package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class ConvertibleByteArrayValue extends AbstractConvertibleValue<byte[]> {

    public ConvertibleByteArrayValue(byte[] bytes, Charset charset) {
        super(bytes, charset);
    }

    @Nonnull
    @Override
    public ConvertibleValue<byte[]> convertToByteArrayValue() {
        return this;
    }

    @Nonnull
    @Override
    public ConvertibleValue<Reader> convertToReaderValue() {
        return convertToInputStreamValue().convertToReaderValue();
    }

    @Nullable
    @Override
    protected InputStream convertToInputStream() {
        final byte[] bytes = this.get();
        return (bytes != null) ? new ByteArrayInputStream(bytes) : null;
    }

    @Nullable
    @Override
    protected String convertToString() {
        final byte[] bytes = this.get();
        return (bytes != null) ? new String(bytes, this.getCharset()) : null;
    }

    @Nonnull
    @Override
    public ConvertibleValue<byte[]> changeCharset(@Nonnull Charset charset) {
        return new ConvertibleByteArrayValue(this.get(), charset);
    }


}
