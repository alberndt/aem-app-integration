package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.function.Function;

public class ConvertibleObjectValue<T> extends AbstractConvertibleValue<T> {

    private final Function<T, ?> converter;

    public ConvertibleObjectValue(T value, Charset charset, Function<T, ?> converter) {
        super(value, charset);
        this.converter = converter;
    }

    @Nonnull
    @Override
    public ConvertibleValue<InputStream> convertToInputStreamValue() {
        return convertToBasicConvertibleResource().convertToInputStreamValue();
    }

    @Nonnull
    @Override
    public ConvertibleValue<Reader> convertToReaderValue() {
        return convertToBasicConvertibleResource().convertToReaderValue();
    }

    @Nonnull
    @Override
    public ConvertibleValue<byte[]> convertToByteArrayValue() throws IOException {
        return convertToBasicConvertibleResource().convertToByteArrayValue();
    }

    @Nonnull
    @Override
    public ConvertibleValue<String> convertToStringValue() throws IOException {
        return convertToBasicConvertibleResource().convertToStringValue();
    }

    private ConvertibleValue<?> convertToBasicConvertibleResource() {
        final T value = this.get();
        if (value == null)
            return new ConvertibleStringValue(null, this.getCharset());

        final Object convertedValue = converter.apply(value);
        if (convertedValue == null)
            return new ConvertibleStringValue(null, this.getCharset());
        if (convertedValue instanceof String)
            return new ConvertibleStringValue((String) convertedValue, this.getCharset());
        if (convertedValue instanceof byte[])
            return new ConvertibleByteArrayValue((byte[]) convertedValue, this.getCharset());
        if (convertedValue instanceof Reader)
            return new ConvertibleReaderValue((Reader) convertedValue, this.getCharset());
        if (convertedValue instanceof InputStream)
            return new ConvertibleInputStreamValue((InputStream) convertedValue, this.getCharset());

        throw new IllegalArgumentException(String.format("Convertible resource of type %s could only be convert to %s. "
                        + "But it should be either String, byte[], Reader or InputStream!",
                value.getClass().getSimpleName(), (convertedValue.getClass().getSimpleName())));
    }

    @Nonnull
    @Override
    public ConvertibleValue<T> changeCharset(@Nonnull Charset charset) {
        return new ConvertibleObjectValue<>(this.get(), charset, this.converter);
    }
}
