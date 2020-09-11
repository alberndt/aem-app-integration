package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class TextParserTest {

    @Test
    void testDefaultMethods() throws IOException {

        TestTextParser1 textParser1 = new TestTextParser1();

        assertEquals(String.class, textParser1.getTargetType());
        assertEquals("Hello", textParser1.parse(new StringReader("Hello World")));
        assertFalse(textParser1.isSerializeSupported());
        assertThrows(UnsupportedOperationException.class, () -> textParser1.serialize("Hello"));

        assertEquals("Hello", textParser1.requireSourceType("Hello", String.class));
        assertThrows(ConversionException.class, () -> textParser1.requireSourceType(42, String.class));
    }

    private static class TestTextParser1 implements TextParser {

        @Nonnull
        @Override
        public Class<?> getTargetType() {
            return String.class;
        }

        @Override
        public Object parse(@Nonnull Reader reader) {
            return "Hello";
        }
    }

}