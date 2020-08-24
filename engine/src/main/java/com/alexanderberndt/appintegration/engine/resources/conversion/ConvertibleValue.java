package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

@Immutable
public interface ConvertibleValue<T> {

    @Nullable
    T get();

    @Nonnull
    Charset getCharset();

    @Nullable
    <C> C get(@Nonnull Class<C> expectedType);

    @Nonnull
    ConvertibleValue<InputStream> convertToInputStreamValue();

    @Nonnull
    ConvertibleValue<Reader> convertToReaderValue();

    @Nonnull
    ConvertibleValue<byte[]> convertToByteArrayValue() throws IOException;

    @Nonnull
    ConvertibleValue<String> convertToStringValue() throws IOException;

    @Nonnull
    ConvertibleValue<T> changeCharset(@Nonnull Charset charset);

}
