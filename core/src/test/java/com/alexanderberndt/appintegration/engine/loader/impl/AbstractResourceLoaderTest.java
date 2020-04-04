
package com.alexanderberndt.appintegration.engine.loader.impl;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class AbstractResourceLoaderTest {

    private static final String TEST_STRING = "Hello World with Umlaute öäüÜÄÖß and á âûè€." + System.lineSeparator();

    private static final TestResourceLoader resourceLoader1000 = new TestResourceLoader(TEST_STRING, 1000, null);


    @Test
    void loadAsString() throws IOException {
        String data = resourceLoader1000.load("xxx", String.class);
        assertEquals(1000 * TEST_STRING.length(), data.length());
    }

    @Test
    void loadAsByteArray() throws IOException {
        byte[] data = resourceLoader1000.load("xxx", byte[].class);
        assertTrue(1000 * TEST_STRING.length() <= data.length);
    }

    @Test
    void loadAsInputStream() throws IOException {
        InputStream inputStream = resourceLoader1000.load("xxx", InputStream.class);
        assertNotNull(inputStream);
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
        for (int i = 0; i < 1000; i++) {
            assertEquals(TEST_STRING, lineNumberReader.readLine() + System.lineSeparator());
        }
        assertNull(lineNumberReader.readLine());
    }

    @Test
    void loadAsReader() throws IOException {
        Reader reader = resourceLoader1000.load("xxx", Reader.class);
        assertNotNull(reader);
        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        for (int i = 0; i < 1000; i++) {
            assertEquals(TEST_STRING, lineNumberReader.readLine() + System.lineSeparator());
        }
        assertNull(lineNumberReader.readLine());
    }



    /**
     * Internal Testclass
     */
    private static class TestResourceLoader extends AbstractResourceLoader {

        private final String charset;

        private final byte[] byteArray;

        public TestResourceLoader(String testString, int recurrence, String charset) {
            this.charset = charset;
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                for (int i = 0; i < recurrence; i++) {
                    if (charset != null) {
                        byteArrayOutputStream.write(testString.getBytes(charset));
                    } else {
                        byteArrayOutputStream.write(testString.getBytes());
                    }
                }
            } catch (IOException e) {

            }
            this.byteArray = byteArrayOutputStream.toByteArray();
        }

        @Override
        protected AttributedInputStream loadInternal(String url) {
            AttributedInputStream inputStream = new AttributedInputStream(new ByteArrayInputStream(byteArray));
            if (charset != null) {
                inputStream.setCharset(charset);
            }
            return inputStream;
        }

        @Override
        public String resolveRelativeUrl(String baseUrl, String relativeUrl) {
            return relativeUrl;
        }
    }

}