package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.input.ReaderInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

@Immutable
public final class ConvertibleValue<T> {

    @Nonnull
    private final LazyValue<T> lazyValue;

    @Nonnull
    private final Charset charset;

    @Nonnull
    private final TextParserSupplier textParsersSupplier;


    private ConvertibleValue(@Nonnull LazyValue<T> lazyValue, @Nonnull Charset charset, @Nullable TextParserSupplier textParsersSupplier) {
        this.lazyValue = lazyValue;
        this.charset = charset;
        this.textParsersSupplier = (textParsersSupplier != null) ? textParsersSupplier : () -> null;
    }

    public ConvertibleValue(@Nonnull T value, @Nonnull Charset charset, @Nullable TextParserSupplier textParsersSupplier) {
        this(new LazyValue<>(value), charset, textParsersSupplier);
    }

    @Nonnull
    public T get() {
        return lazyValue.get();
    }

    @Nonnull
    public Charset getCharset() {
        return charset;
    }

    @Nonnull
    public ConvertibleValue<T> recreateWithNewCharset(@Nonnull Charset newCharset) {
        return new ConvertibleValue<>(lazyValue, newCharset, textParsersSupplier);
    }

    @Nonnull
    public <C> ConvertibleValue<C> recreateWithNewContent(@Nonnull C newValue) {
        return new ConvertibleValue<>(newValue, charset, textParsersSupplier);
    }

    @Nonnull
    public <C> ConvertibleValue<C> recreateWithSupplier(@Nonnull Supplier<C> valueSupplier, @Nonnull Class<C> type) {
        return new ConvertibleValue<>(new LazyValue<>(valueSupplier, type), charset, textParsersSupplier);
    }

    @Nonnull
    public ConvertibleValue<InputStream> convertToInputStreamValue() throws IOException {
        return convertTo(InputStream.class);
    }

    @Nonnull
    public ConvertibleValue<Reader> convertToReaderValue() throws IOException {
        return convertTo(Reader.class);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <C> ConvertibleValue<C> convertTo(@Nonnull Class<C> targetClass) throws IOException {

        // is no conversion needed? (plus null checking, to avoid warnings)
        if (targetClass.isAssignableFrom(lazyValue.getType())) {
            return (ConvertibleValue<C>) this;
        }

        if (lazyValue.isInstanceOf(InputStream.class)) {
            // convert to Reader
            ConvertibleValue<Reader> readerValue = recreateWithNewContent(new InputStreamReader((InputStream) this.lazyValue.get(), charset));
            if (targetClass.equals(Reader.class)) {
                return (ConvertibleValue<C>) readerValue;
            } else {
                return readerValue.convertTo(targetClass);
            }
        } else if (lazyValue.isInstanceOf(Reader.class)) {
            if (targetClass.equals(InputStream.class)) {
                return (ConvertibleValue<C>) recreateWithNewContent(new ReaderInputStream((Reader) this.lazyValue.get(), this.charset));
            } else {
                final C parsedObj = parse((Reader) this.lazyValue.get(), targetClass);
                return recreateWithNewContent(parsedObj);
            }
        } else {
            final Reader reader = serialize(this.lazyValue.get());
            return recreateWithNewContent(reader).convertTo(targetClass);
        }
    }

    @SuppressWarnings("unchecked")
    private <C> C parse(Reader reader, @Nonnull Class<C> targetClass) throws IOException {
        final TextParser textParser = requireTextParser(targetClass);
        final Object parsedObj = textParser.parse(reader);
        if (targetClass.isInstance(parsedObj)) {
            return (C) parsedObj;
        } else {
            throw new ConversionException(
                    String.format("TextParser %s was expected to return type %s, but returned %s!",
                            textParser.getClass().getSimpleName(), targetClass.getName(), parsedObj.getClass().getName()));
        }
    }

    @Nonnull
    private Reader serialize(T value) throws IOException {
        final TextParser textParser = requireTextSerializer(lazyValue.getType());
        final String content = textParser.serialize(value);
        return new StringReader(content);
    }

    @Nonnull
    private TextParser requireTextParser(@Nonnull Class<?> targetClass) throws ConversionException {
        final Collection<TextParser> textParsers = textParsersSupplier.get();
        for (TextParser textParser : textParsers) {
            if (targetClass.isAssignableFrom(textParser.getTargetType())) {
                return textParser;
            }
        }
        throw new ConversionException("Cannot parse to " + targetClass);
    }

    @Nonnull
    private TextParser requireTextSerializer(@Nonnull Class<?> sourceClass) throws ConversionException {
        final Collection<TextParser> textParsers = textParsersSupplier.get();
        for (TextParser textParser : textParsers) {
            if (textParser.getTargetType().isAssignableFrom(sourceClass)) {
                return textParser;
            }
        }
        throw new ConversionException("Cannot serialize " + sourceClass);
    }

    private static class LazyValue<T> {

        @Nullable
        private Supplier<T> valueSupplier;

        @Nullable
        private T resolvedValue;

        @Nonnull
        private final Class<T> type;


        public LazyValue(@Nonnull Supplier<T> valueSupplier, @Nonnull Class<T> type) {
            this.valueSupplier = valueSupplier;
            this.resolvedValue = null;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public LazyValue(@Nonnull T value) {
            this.valueSupplier = null;
            this.resolvedValue = value;
            this.type = (Class<T>) value.getClass();
        }

        @Nonnull
        public T get() {
            if (valueSupplier != null) {
                resolvedValue = valueSupplier.get();
                valueSupplier = null;
            }
            return Objects.requireNonNull(resolvedValue);
        }

        @Nonnull
        public Class<T> getType() {
            return type;
        }

        public boolean isInstanceOf(@Nonnull Class<?> targetType) {
            return targetType.isAssignableFrom(type);
        }
    }

}
