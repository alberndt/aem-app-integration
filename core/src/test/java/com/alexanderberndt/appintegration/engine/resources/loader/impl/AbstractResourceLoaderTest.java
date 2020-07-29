
package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class AbstractResourceLoaderTest {

    private static final String TEST_STRING = "Hello World with Umlaute öäüÜÄÖß and á âûè€." + System.lineSeparator();

    private static final TestResourceLoader resourceLoader1000 = new TestResourceLoader(TEST_STRING, 1000, null);

    private static final ExternalResourceRef resourceRef = new ExternalResourceRef("xxx", ExternalResourceType.PLAIN_TEXT);


    @Test
    void loadAsString() throws IOException {

        ExternalResource resource = resourceLoader1000.load("xxx", resourceRef);

        assertNotNull(resource);
        assertEquals(1000 * TEST_STRING.length(), resource.getString().length());
    }

    @Test
    void loadAsByteArray() throws IOException {

        ExternalResource resource = resourceLoader1000.load("xxx", resourceRef);

        assertNotNull(resource);
        assertTrue(1000 * TEST_STRING.length() <= resource.getBytes().length);
    }

    @Test
    void loadAsInputStream() throws IOException {

        ExternalResource resource = resourceLoader1000.load("xxx", resourceRef);

        assertNotNull(resource);
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(resource.getInputStream()));
        for (int i = 0; i < 1000; i++) {
            assertEquals(TEST_STRING, lineNumberReader.readLine() + System.lineSeparator());
        }
        assertNull(lineNumberReader.readLine());
    }

    @Test
    void loadAsReader() throws IOException {

        ExternalResource resource = resourceLoader1000.load("xxx", resourceRef);

        assertNotNull(resource);

        LineNumberReader lineNumberReader = new LineNumberReader(resource.getReader());
        for (int i = 0; i < 1000; i++) {
            assertEquals(TEST_STRING, lineNumberReader.readLine() + System.lineSeparator());
        }
        assertNull(lineNumberReader.readLine());
    }



    /**
     * Internal Testclass
     */
    private static class TestResourceLoader implements ResourceLoader {

        private final Charset charset;

        private final byte[] byteArray;

        public TestResourceLoader(String testString, int recurrence, Charset charset) {
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
                e.printStackTrace();
            }
            this.byteArray = byteArrayOutputStream.toByteArray();
        }

        @Override
        public ExternalResource load(String baseUrl, ExternalResourceRef resourceRef) {
            ExternalResource externalResource = new ExternalResource(resourceRef);
            externalResource.setInputStream(new ByteArrayInputStream(byteArray));
            if (charset != null) {
                externalResource.setCharset(charset);
            }
            return externalResource;
        }
    }

}