package com.alexanderberndt.appintegration.engine.resources.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.function.Function;

public class ConvertibleObjectResource<T> extends AbstractConvertibleResource<T> {

    private final Function<T, ?> converter;

    public ConvertibleObjectResource(T value, Charset charset, Function<T, ?> converter) {
        super(value, charset);
        this.converter = converter;
    }

    @Override
    public ConvertibleResource<InputStream> convertToInputStreamResource() {
        return convertToBasicConvertibleResource().convertToInputStreamResource();
    }

    @Override
    public ConvertibleResource<Reader> convertToReaderResource() {
        return convertToBasicConvertibleResource().convertToReaderResource();
    }

    @Override
    public ConvertibleResource<byte[]> convertToByteArrayResource() throws IOException {
        return convertToBasicConvertibleResource().convertToByteArrayResource();
    }

    @Override
    public ConvertibleResource<String> convertToStringResource() throws IOException {
        return convertToBasicConvertibleResource().convertToStringResource();
    }

    private ConvertibleResource<?> convertToBasicConvertibleResource() {
        final T value = this.get();
        if (value == null)
            return new ConvertibleStringResource(null, this.getCharset());

        final Object convertedValue = converter.apply(value);
        if (convertedValue == null)
            return new ConvertibleStringResource(null, this.getCharset());
        if (convertedValue instanceof String)
            return new ConvertibleStringResource((String) convertedValue, this.getCharset());
        if (convertedValue instanceof byte[])
            return new ConvertibleByteArrayResource((byte[]) convertedValue, this.getCharset());
        if (convertedValue instanceof Reader)
            return new ConvertibleReaderResource((Reader) convertedValue, this.getCharset());
        if (convertedValue instanceof InputStream)
            return new ConvertibleInputStreamResource((InputStream) convertedValue, this.getCharset());

        throw new IllegalArgumentException(String.format("Convertible resource of type %s could only be convert to %s. "
                        + "But it should be either String, byte[], Reader or InputStream!",
                value.getClass().getSimpleName(), (convertedValue.getClass().getSimpleName())));
    }
}
