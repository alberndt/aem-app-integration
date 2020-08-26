package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

public interface TextParser {

    @Nonnull
    Class<?> getTargetType();

    Object parse(@Nonnull Reader reader) throws IOException;

    default boolean isSerializeSupported() {
        return false;
    }

    default String serialize(@Nonnull Object source) throws IOException {
        throw new UnsupportedOperationException("method not implemented");
    }

    @SuppressWarnings("unchecked")
    default <T> T requireSourceType(@Nonnull Object source, @Nonnull Class<T> expectedType) throws ConversionException {
        if (expectedType.isInstance(source)) {
            return (T) source;
        } else {
            throw new ConversionException(String.format("Object to serialize must instance of %s, but is %s!",
                    expectedType.getName(), source.getClass().getName()));
        }
    }

}
