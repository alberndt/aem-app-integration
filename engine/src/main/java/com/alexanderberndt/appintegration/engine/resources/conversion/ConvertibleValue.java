package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.input.ReaderInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;

@Immutable
public final class ConvertibleValue<T> {

    @Nullable
    private final T value;

    @Nonnull
    private final Charset charset;

    @Nonnull
    private final TextParserSupplier textParsersSupplier;

    public ConvertibleValue(@Nullable T value, @Nonnull Charset charset, @Nullable TextParserSupplier textParsersSupplier) {
        this.value = value;
        this.charset = charset;
        this.textParsersSupplier = (textParsersSupplier != null) ? textParsersSupplier : () -> null;
    }

    @Nullable
    public T get() {
        return value;
    }

    @Nonnull
    public Charset getCharset() {
        return charset;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Class<T> getValueType() {
        return (value != null) ? (Class<T>) value.getClass() : null;
    }

    @Nonnull
    public ConvertibleValue<T> recreateWithNewCharset(@Nonnull Charset newCharset) {
        return new ConvertibleValue<>(value, newCharset, textParsersSupplier);
    }

    @Nonnull
    public <C> ConvertibleValue<C> recreateWithNewContent(@Nullable C newValue) {
        return new ConvertibleValue<>(newValue, charset, textParsersSupplier);
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
        if ((this.value == null) || (this.getValueType() == null) || targetClass.isInstance(value)) {
            return (ConvertibleValue<C>) this;
        }

        if (this.value instanceof InputStream) {
            // convert to Reader
            ConvertibleValue<Reader> readerValue = recreateWithNewContent(new InputStreamReader((InputStream) this.value, charset));
            if (targetClass.equals(Reader.class)) {
                return (ConvertibleValue<C>) readerValue;
            } else {
                return readerValue.convertTo(targetClass);
            }
        } else if (this.value instanceof Reader) {
            if (targetClass.equals(InputStream.class)) {
                return (ConvertibleValue<C>) recreateWithNewContent(new ReaderInputStream((Reader) this.value, this.charset));
            } else {
                final C parsedObj = parse((Reader) this.value, targetClass);
                return recreateWithNewContent(parsedObj);
            }
        } else {
            final Reader reader = serialize(this.value);
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

    private Reader serialize(T value) throws IOException {
        final Class<T> sourceClass = this.getValueType();
        if (sourceClass != null) {
            final TextParser textParser = requireTextSerializer(sourceClass);
            final String content = textParser.serialize(value);
            return new StringReader(content);
        } else {
            return null;
        }
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
            if (textParser.isSerializeSupported() && textParser.getTargetType().isAssignableFrom(sourceClass)) {
                return textParser;
            }
        }
        throw new ConversionException("Cannot serialize " + sourceClass);
    }

}
