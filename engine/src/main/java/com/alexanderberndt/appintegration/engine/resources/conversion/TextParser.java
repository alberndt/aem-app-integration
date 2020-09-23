package com.alexanderberndt.appintegration.engine.resources.conversion;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

public interface TextParser {

    @Nonnull
    Class<?> getTargetType();

    Object parse(@Nonnull Reader reader) throws IOException;

    String serialize(@Nonnull Object source) throws IOException;

}
