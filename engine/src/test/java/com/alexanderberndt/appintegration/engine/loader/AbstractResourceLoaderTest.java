
package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AbstractResourceLoaderTest {

    private static final String TEST_STRING = "Hello World with Umlaute öäüÜÄÖß and á âûè€." + System.lineSeparator();

    private static final TestResourceLoader resourceLoader1000 = new TestResourceLoader(TEST_STRING, 1000, null);

    private static final ExternalResourceRef resourceRef = ExternalResourceRef.create("xxx", ExternalResourceType.PLAIN_TEXT);

    @Test
    void loadAsString() throws IOException {
        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);
        assertNotNull(resource);
        assertEquals(1000 * TEST_STRING.length(), resource.getContentAsParsedObject(String.class).length());
    }

    @Test
    void loadAsInputStream() throws IOException {
        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);
        assertNotNull(resource);
        assert1000Lines(new LineNumberReader(new InputStreamReader(resource.getContentAsInputStream())));
    }

    @Test
    void loadAsReader() throws IOException {
        ExternalResource resource = resourceLoader1000.load(resourceRef, this::createExternalResource);
        assertNotNull(resource);
        assert1000Lines(new LineNumberReader(resource.getContentAsReader()));
    }

    @Nonnull
    protected ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, Map<String, Object> metadataMap) {
        return new ExternalResource(uri, type, content, metadataMap, () -> Collections.singletonList(new StringConverter()));
    }


    private void assert1000Lines(LineNumberReader lineNumberReader) throws IOException {
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

        @Nonnull
        @Override
        public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) {
            ExternalResource externalResource = factory.createExternalResource(resourceRef, new ByteArrayInputStream(byteArray));
            if (charset != null) {
                externalResource.setCharset(charset);
            }
            return externalResource;
        }
    }

}