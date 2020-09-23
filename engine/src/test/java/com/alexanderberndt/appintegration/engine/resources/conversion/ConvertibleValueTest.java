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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConvertibleValueTest {

    public static final String TEST_DATA = "Hello World,\n"
            + "with some new lines.\n"
            + "Thank you!";

    private static final List<TextParser> textParsers = Arrays.asList(new StringConverter(), new MyParser());

    private static final TextParserSupplier CONVERSION_SUPPLIER = () -> textParsers;

    private static Stream<Arguments> provideInputValues() {
        return Stream.of(
                Arguments.of("String Value", new ConvertibleValue<>(TEST_DATA, StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
                Arguments.of("Parsed Text", new ConvertibleValue<>(new ParsedText5(TEST_DATA), StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
                Arguments.of("Reader Value", new ConvertibleValue<>(new StringReader(TEST_DATA), StandardCharsets.UTF_8, CONVERSION_SUPPLIER)),
                Arguments.of("InputStream Value", new ConvertibleValue<>(new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, CONVERSION_SUPPLIER))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToString(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<String> resource = inputResource.convertTo(String.class);
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
    void convertToInputStream(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<InputStream> resource = inputResource.convertToInputStreamValue();
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");

        final String value = IOUtils.toString(resource.get(), StandardCharsets.UTF_8);
        assertEquals(TEST_DATA, value, "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToParsedText(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<ParsedText> resource = inputResource.convertTo(ParsedText.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get().getText(), "conversion of " + inputTypeName + " should return expected result");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("provideInputValues")
    void convertToParsedText2(String inputTypeName, ConvertibleValue<?> inputResource) throws IOException {
        ConvertibleValue<ParsedText2> resource = inputResource.convertTo(ParsedText2.class);
        assertNotNull(resource, "conversion of " + inputTypeName + " should return something");
        assertEquals(TEST_DATA, resource.get().getText(), "conversion of " + inputTypeName + " should return expected result");
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

    private static class MyParser extends AbstractTextParser<ParsedText2> {

        public MyParser() {
            super(ParsedText2.class);
        }

        @Override
        public ParsedText2 parse(@Nonnull Reader reader) throws IOException {
            ParsedText2 parsedText2 = new ParsedText4();
            parsedText2.setText(IOUtils.toString(reader));
            return parsedText2;
        }

        @Override
        protected String serializeType(@Nonnull ParsedText2 source) throws IOException {
            return source.getText();
        }
    }

}