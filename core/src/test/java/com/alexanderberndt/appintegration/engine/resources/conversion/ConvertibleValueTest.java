package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConvertibleValueTest {

    public static final String TEST_DATA = "Hello World,\n"
            + "with some new lines.\n"
            + "Thank you!";

    private static Stream<Arguments> provideInputValues() {
        return Stream.of(
                Arguments.of("String Value", new ConvertibleStringValue(TEST_DATA, StandardCharsets.UTF_8)),
                Arguments.of("Reader Value", new ConvertibleReaderValue(new StringReader(TEST_DATA), StandardCharsets.UTF_8)),
                Arguments.of("ByteArray Value", new ConvertibleByteArrayValue(TEST_DATA.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
                Arguments.of("InputStream Value", new ConvertibleInputStreamValue(new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)),
                Arguments.of("Object Value with String-Converter", new ConvertibleObjectValue<>(
                        42, StandardCharsets.UTF_8, i -> (i == 42) ? TEST_DATA : "something else")),
                Arguments.of("Object Value with Reader-Converter", new ConvertibleObjectValue<>(
                        42, StandardCharsets.UTF_8, i -> new StringReader((i == 42) ? TEST_DATA : "something else"))),
                Arguments.of("Object Value with ByteArray-Converter", new ConvertibleObjectValue<>(
                        42, StandardCharsets.UTF_8, i -> ((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))),
                Arguments.of("Object Value with InputStream-Converter", new ConvertibleObjectValue<>(
                        42, StandardCharsets.UTF_8, i -> new ByteArrayInputStream(((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))))
        );
    }

    private static Stream<Arguments> provideNullValues() {
        return Stream.of(
                Arguments.of("Null Value", new ConvertibleNullValue(StandardCharsets.UTF_8)),
                Arguments.of("String Value", new ConvertibleStringValue(null, StandardCharsets.UTF_8)),
                Arguments.of("Reader Value", new ConvertibleReaderValue(null, StandardCharsets.UTF_8)),
                Arguments.of("ByteArray Value", new ConvertibleByteArrayValue(null, StandardCharsets.UTF_8)),
                Arguments.of("InputStream Value", new ConvertibleInputStreamValue(null, StandardCharsets.UTF_8)),
                Arguments.of("Object Value with String-Converter", new ConvertibleObjectValue<Integer>(
                        null, StandardCharsets.UTF_8, i -> "something else")),
                Arguments.of("Object Value with Reader-Converter", new ConvertibleObjectValue<Integer>(
                        null, StandardCharsets.UTF_8, i -> new StringReader((i == null) ? null : "something else"))),
                Arguments.of("Object Value with ByteArray-Converter", new ConvertibleObjectValue<Integer>(
                        null, StandardCharsets.UTF_8, i -> ((i == null) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))),
                Arguments.of("Object Value with InputStream-Converter", new ConvertibleObjectValue<Integer>(
                        null, StandardCharsets.UTF_8, i -> new ByteArrayInputStream(((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToString(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<String> resource = inputResource.convertToStringValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToReader(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<Reader> resource = inputResource.convertToReaderValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get());
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToByteArray(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<byte[]> resource = inputResource.convertToByteArrayValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, new String(resource.get(), StandardCharsets.UTF_8), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToInputStream(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<InputStream> resource = inputResource.convertToInputStreamValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get(), StandardCharsets.UTF_8);
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToString(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<String> resource = inputResource.convertToStringValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToReader(String inputTypeName, ConvertibleValue<?> inputResource) {
        ConvertibleValue<Reader> resource = inputResource.convertToReaderValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToByteArray(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<byte[]> resource = inputResource.convertToByteArrayValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToInputStream(String inputTypeName, ConvertibleValue<?> inputResource) {
        ConvertibleValue<InputStream> resource = inputResource.convertToInputStreamValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }


}