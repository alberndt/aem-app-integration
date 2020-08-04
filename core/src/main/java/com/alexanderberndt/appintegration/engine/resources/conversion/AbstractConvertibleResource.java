package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

public abstract class AbstractConvertibleResource<T> implements ConvertibleResource<T> {

    private final T value;

    private final Charset charset;

    protected AbstractConvertibleResource(T value, Charset charset) {
        this.value = value;
        this.charset = charset;
    }

    @Override
    public T get() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C get(@Nonnull Class<C> expectedType) {
        if (value == null) return null;
        if (value.getClass() == expectedType) return (C) value;
        throw new IllegalArgumentException(String.format("Resource is of type %s, and cannot be get() as type %s",
                value.getClass().getSimpleName(), expectedType.getSimpleName()));
    }

    @Override
    public Charset getCharset() {
        return charset;
    }
}
