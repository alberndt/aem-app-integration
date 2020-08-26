
package com.alexanderberndt.appintegration.engine.loader.impl;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AbstractResourceLoaderTest {

    private static final String TEST_STRING = "Hello World with Umlaute öäüÜÄÖß and á âûè€." + System.lineSeparator();

    private static final TestResourceLoader resourceLoader1000 = new TestResourceLoader(TEST_STRING, 1000, null);

    private static final ExternalResourceRef resourceRef = new ExternalResourceRef("xxx", ExternalResourceType.PLAIN_TEXT);

    protected ExternalResource createExternalResource(InputStream inputStream, ExternalResourceRef resourceRef, ResourceLoader loader) {
        return new ExternalResource(loader, resourceRef, () -> Collections.singletonList(new StringConverter()));
    }

    @Test
    void loadAsString() throws IOException {

        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);

        assertNotNull(resource);
        assertEquals(1000 * TEST_STRING.length(), resource.getContentAsParsedObject(String.class).length());
    }

//    @Test
//    void loadAsByteArray() throws IOException {
//
//        ExternalResource resource = resourceLoader1000.load(resourceRef);
//
//        assertNotNull(resource);
//        assertTrue(1000 * TEST_STRING.length() <= resource.getContentAsByteArray().length);
//    }

    @Test
    void loadAsInputStream() throws IOException {

        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);

        assertNotNull(resource);
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(resource.getContentAsInputStream()));
        for (int i = 0; i < 1000; i++) {
            assertEquals(TEST_STRING, lineNumberReader.readLine() + System.lineSeparator());
        }
        assertNull(lineNumberReader.readLine());
    }

    @Test
    void loadAsReader() throws IOException {

        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);

        assertNotNull(resource);

        LineNumberReader lineNumberReader = new LineNumberReader(resource.getContentAsReader());
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
        public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) {
            ExternalResource externalResource = factory.createExternalResource(new ByteArrayInputStream(byteArray), resourceRef, this);
            if (charset != null) {
                externalResource.setCharset(charset);
            }
            return externalResource;
        }

        @Override
        public ExternalResourceRef resolveRelativeUrl(String baseUrl, String relativeUrl, ExternalResourceType expectedType) {
            return null;
        }
    }

}