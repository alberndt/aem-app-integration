package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConvertibleValueTest {

    public static final String TEST_DATA = "Hello World,\n"
            + "with some new lines.\n"
            + "Thank you!";

    private static final List<TextParser> textParsers = Arrays.asList(new StringConverter(), new MyParser());

    private static final ConversionSupplier CONVERSION_SUPPLIER = () -> textParsers;

    private static Stream<Arguments> provideInputValues() {
        return Stream.of(
                Arguments.of("String Value", new ConvertibleValue<>(TEST_DATA, StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
                Arguments.of("Parsed Text", new ConvertibleValue<>(new ParsedText5(TEST_DATA), StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
                Arguments.of("Reader Value", new ConvertibleValue<>(new StringReader(TEST_DATA), StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
//                Arguments.of("ByteArray Value", new ConvertibleValue<>(TEST_DATA.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
                Arguments.of("InputStream Value", new ConvertibleValue<>(new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, CONVERSION_SUPPLIER))
//                Arguments.of("Object Value with String-TextParser", new ConvertibleValue<>(
//                        42, StandardCharsets.UTF_8, i -> (i == 42) ? TEST_DATA : "something else")),
//                Arguments.of("Object Value with Reader-TextParser", new ConvertibleValue<>(
//                        42, StandardCharsets.UTF_8, i -> new StringReader((i == 42) ? TEST_DATA : "something else"))),
//                Arguments.of("Object Value with ByteArray-TextParser", new ConvertibleValue<>(
//                        42, StandardCharsets.UTF_8, i -> ((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))),
//                Arguments.of("Object Value with InputStream-TextParser", new ConvertibleValue<>(
//                        42, StandardCharsets.UTF_8, i -> new ByteArrayInputStream(((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))))
        );
    }

    private static Stream<Arguments> provideNullValues() {
        return Stream.of(
                Arguments.of("Null Value", new ConvertibleValue<>(null, StandardCharsets.UTF_8, CONVERSION_SUPPLIER))
//                Arguments.of("String Value", new ConvertibleValue<>(null, StandardCharsets.UTF_8)),
//                Arguments.of("Reader Value", new ConvertibleValue<>(null, StandardCharsets.UTF_8)),
//                Arguments.of("ByteArray Value", new ConvertibleValue<>(null, StandardCharsets.UTF_8)),
//                Arguments.of("InputStream Value", new ConvertibleValue<>(null, StandardCharsets.UTF_8))
//                Arguments.of("Object Value with String-TextParser", new ConvertibleObjectValue<Integer>(
//                        null, StandardCharsets.UTF_8, i -> "something else")),
//                Arguments.of("Object Value with Reader-TextParser", new ConvertibleObjectValue<Integer>(
//                        null, StandardCharsets.UTF_8, i -> new StringReader((i == null) ? null : "something else"))),
//                Arguments.of("Object Value with ByteArray-TextParser", new ConvertibleObjectValue<Integer>(
//                        null, StandardCharsets.UTF_8, i -> ((i == null) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))),
//                Arguments.of("Object Value with InputStream-TextParser", new ConvertibleObjectValue<Integer>(
//                        null, StandardCharsets.UTF_8, i -> new ByteArrayInputStream(((i == 42) ? TEST_DATA : "something else").getBytes(StandardCharsets.UTF_8))))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToString(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<String> resource = inputResource.convertTo(String.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToReader(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<Reader> resource = inputResource.convertToReaderValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get());
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToInputStream(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<InputStream> resource = inputResource.convertToInputStreamValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get(), StandardCharsets.UTF_8);
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToParsedText(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<ParsedText> resource = inputResource.convertTo(ParsedText.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get().getText(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToParsedText2(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<ParsedText2> resource = inputResource.convertTo(ParsedText2.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get().getText(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToString(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<String> resource = inputResource.convertTo(String.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToReader(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<Reader> resource = inputResource.convertToReaderValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }

//    @ParameterizedTest(name = "{index} {0}")
//    @MethodSource("provideNullValues")
//    void convertNullToByteArray(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
//        ConvertibleValue<byte[]> resource = inputResource.convertToByteArrayValue();
//        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
//        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
//    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideNullValues")
    void convertNullToInputStream(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException, ConversionException {
        ConvertibleValue<InputStream> resource = inputResource.convertToInputStreamValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertNull(resource.get(), "conversion of " + inputTypeName + " should return a null-value");
    }


    private static class ParsedText {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private static class ParsedText2 extends ParsedText {
    }

    private static class ParsedText3 extends ParsedText2 {

    }

    private static class ParsedText4 extends ParsedText3 {

    }

    private static class ParsedText5 extends ParsedText4 {

        public ParsedText5(String text) {
            this.setText(text);
        }
    }

    private static class MyParser implements TextParser {

        @Nonnull
        @Override
        public Class<ParsedText2> getTargetType() {
            return ParsedText2.class;
        }

        @Override
        public ParsedText2 parse(@Nonnull Reader reader) throws IOException {
            ParsedText2 parsedText2 = new ParsedText4();
            parsedText2.setText(IOUtils.toString(reader));
            return parsedText2;
        }

        @Override
        public boolean isSerializeSupported() {
            return true;
        }

        @Override
        public String serialize(@Nonnull Object source) throws ConversionException {
            return requireSourceType(source, ParsedText2.class).getText();
        }


    }

}