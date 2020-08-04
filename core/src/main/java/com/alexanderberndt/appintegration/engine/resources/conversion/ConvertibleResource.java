package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public interface ConvertibleResource<T> {

    T get();

    <C> C get(@Nonnull Class<C> expectedType);

    Charset getCharset();

    ConvertibleResource<InputStream> convertToInputStreamResource();

    ConvertibleResource<Reader> convertToReaderResource();

    ConvertibleResource<byte[]> convertToByteArrayResource() throws IOException;

    ConvertibleResource<String> convertToStringResource() throws IOException;

}
