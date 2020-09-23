package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.io.IOException;

public abstract class AbstractTextParser<T> implements TextParser {

    @Nonnull
    private final Class<T> targetType;

    public AbstractTextParser(@Nonnull Class<T> targetType) {
        this.targetType = targetType;
    }

    protected abstract String serializeType(@Nonnull T source) throws IOException;

    @Nonnull
    @Override
    public Class<?> getTargetType() {
        return targetType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final String serialize(@Nonnull Object source) throws IOException {
        if (getTargetType().isInstance(source)) {
            return serializeType((T) source);
        } else {
            throw new ConversionException(String.format("Object to serialize must instance of %s, but is %s!",
                    getTargetType().getName(), source.getClass().getName()));
        }
    }

}
