package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TextParserTest {


    @Test
    void convert() {

        Class<?> sourceClass = BufferedInputStream.class;
        Class<?> targetClass = InputStream.class;

        assertTrue(targetClass.isAssignableFrom(sourceClass));


        // Class A given


        // TextParser for Class B exists
        // Task requires Class C


    }

}