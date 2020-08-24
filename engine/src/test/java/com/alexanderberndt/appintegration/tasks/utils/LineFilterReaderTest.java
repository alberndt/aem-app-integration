package com.alexanderberndt.appintegration.tasks.utils;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LineFilterReaderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "hello world!\nthis is a test\n",
            "hello world!\nthis is a test",
            "hello world!\rthis is a test\r",
            "hello world!\rthis is a test",
            "hello world!\r\nthis is a test\r\n",
            "hello world!\r\nthis is a test",
            "hello world!\n\rthis is a test\n\r",
            "hello world!\n\rthis is a test",
            "hello world!",
            ""})
    void testFilter(String input) throws IOException {
        Reader reader = new StringReader(input);
        TestLineFilterReader filter = new TestLineFilterReader(reader);

        StringWriter writer = new StringWriter();
        IOUtils.copy(filter, writer);

        // assert equal output
        assertEquals(input, writer.toString());

        // assert all lines are filtered
        List<String> expectedLines = new BufferedReader(new StringReader(input)).lines().collect(Collectors.toList());
        assertEquals(expectedLines, filter.getLines());
    }

    private static class TestLineFilterReader extends LineFilterReader {

        private final List<String> lines = new ArrayList<>();

        public TestLineFilterReader(Reader input) {
            super(input);
        }

        @Override
        protected String filterLine(String line) throws IOException {
            lines.add(line);
            return line;
        }

        public List<String> getLines() {
            return lines;
        }
    }

}