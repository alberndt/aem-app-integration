package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConvertibleResourceTest {

    public static final String TEST_DATA = "Hello World,\n"
            + "with some new lines.\n"
            + "Thank you!";

    private static Stream<Arguments> provideInputResources() {
        return Stream.of(
                Arguments.of("String Resource", new ConvertibleStringResource(TEST_DATA, StandardCharsets.UTF_8)),
                Arguments.of("Reader Resource", new ConvertibleReaderResource(new StringReader(TEST_DATA), StandardCharsets.UTF_8)),
                Arguments.of("ByteArray Resource", new ConvertibleByteArrayResource(TEST_DATA.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
                Arguments.of("InputStream Resource", new ConvertibleInputStreamResource(new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)),
                Arguments.of("Object Resource with String-Converter", new ConvertibleObjectResource<>(
                        42, StandardCharsets.UTF_8, i -> (i == 42) ? TEST_DATA : "something else")),
                Arguments.of("Object Resource with Reader-Converter", new ConvertibleObjectResource<>(
                        42, StandardCharsets.UTF_8, i -> new StringReader((i == 42) ? TEST_DATA : "something else"))),
                Arguments.of("Object Resource with ByteArray-Converter", new ConvertibleObjectResource<>(
                        42, StandardCharsets.UTF_8, i -> ((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))),
                Arguments.of("Object Resource with InputStream-Converter", new ConvertibleObjectResource<>(
                        42, StandardCharsets.UTF_8, i -> new ByteArrayInputStream(((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputResources")
    void convertToString(String inputTypeName, ConvertibleResource<?> inputResource) throws IOException {
        ConvertibleResource<String> resource = inputResource.convertToStringResource();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputResources")
    void convertToReader(String inputTypeName, ConvertibleResource<?> inputResource) throws IOException {
        ConvertibleResource<Reader> resource = inputResource.convertToReaderResource();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get());
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputResources")
    void convertToByteArray(String inputTypeName, ConvertibleResource<?> inputResource) throws IOException {
        ConvertibleResource<byte[]> resource = inputResource.convertToByteArrayResource();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, new String(resource.get(), StandardCharsets.UTF_8), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputResources")
    void convertToInputStream(String inputTypeName, ConvertibleResource<?> inputResource) throws IOException {
        ConvertibleResource<InputStream> resource = inputResource.convertToInputStreamResource();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get(), StandardCharsets.UTF_8);
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }


}