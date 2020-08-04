package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

@Immutable
public abstract class AbstractConvertibleValue<T> implements ConvertibleValue<T>, Cloneable {

    @Nullable
    private final T value;

    @Nonnull
    private final Charset charset;

    protected AbstractConvertibleValue(@Nullable T value, @Nonnull Charset charset) {
        this.value = value;
        this.charset = charset;
    }

    @Override
    @Nullable
    public final T get() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <C> C get(@Nonnull Class<C> expectedType) {
        if (value == null) return null;
        if (value.getClass() == expectedType) return (C) value;
        throw new IllegalArgumentException(String.format("Resource is of type %s, and cannot be get() as type %s",
                value.getClass().getSimpleName(), expectedType.getSimpleName()));
    }

    @Nonnull
    @Override
    public final Charset getCharset() {
        return charset;
    }

    @Nonnull
    @Override
    public ConvertibleValue<InputStream> convertToInputStreamValue() {
        return new ConvertibleInputStreamValue(this.convertToInputStream(), this.getCharset());
    }

    @Nonnull
    @Override
    public ConvertibleValue<Reader> convertToReaderValue() {
        return new ConvertibleReaderValue(this.convertToReader(), this.getCharset());
    }

    @Nonnull
    @Override
    public ConvertibleValue<byte[]> convertToByteArrayValue() throws IOException {
        return new ConvertibleByteArrayValue(this.convertToByteArray(), this.getCharset());
    }

    @Nonnull
    @Override
    public ConvertibleValue<String> convertToStringValue() throws IOException {
        return new ConvertibleStringValue(convertToString(), this.getCharset());
    }

    @Nullable
    protected InputStream convertToInputStream() {
        throw new UnsupportedOperationException("convertToInputStream() not implemented");
    }

    @Nullable
    protected Reader convertToReader() {
        throw new UnsupportedOperationException("convertToReader() not implemented");
    }

    @Nullable
    protected byte[] convertToByteArray() throws IOException {
        throw new UnsupportedOperationException("convertToByteArray() not implemented");
    }

    @Nullable
    protected String convertToString() throws IOException {
        throw new UnsupportedOperationException("convertToString() not implemented");
    }

}
