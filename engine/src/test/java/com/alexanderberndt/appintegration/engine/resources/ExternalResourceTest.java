package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.resources.conversion.AbstractTextParser;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExternalResourceTest {

    private ExternalResource resource;

    private ExternalResource faultyResource;

    @BeforeEach
    void beforeEach() {
        resource = new ExternalResource(new ByteArrayInputStream("Hello World!".getBytes()), ExternalResourceRef.create("http://www.example.com/data/test-data.txt"), () -> Collections.singleton(new StringConverter()));
        resource.setCharset(StandardCharsets.UTF_8);

//        final InputStream faultyInputStream = Mockito.mock(InputStream.class);
//        Mockito.lenient().when(faultyInputStream.read()).thenThrow(IOException.class);
//        Mockito.lenient().when(faultyInputStream.read(Mockito.any(byte[].class))).thenThrow(IOException.class);
//        Mockito.lenient().when(faultyInputStream.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).thenThrow(IOException.class);
        faultyResource = new ExternalResource(new ByteArrayInputStream(new byte[0]), ExternalResourceRef.create("test-data2.txt"), () -> Collections.singletonList(new FaultyStringConverter()));
        faultyResource.setContentAsParsedObject("I'm faulty!");
    }


    @Test
    void setMetadata() {
        resource.setMetadata("hello", "world");
        assertEquals(1, resource.getMetadataMap().size());
        assertEquals("world", resource.getMetadata("hello", String.class));
        assertNull(resource.getMetadata("hello", Integer.class));
        assertNull(resource.getMetadata("hello2", String.class));
    }

    @Test
    void getContentAsInputStream() throws IOException {
        assertEquals("Hello World!", IOUtils.toString(resource.getContentAsInputStream(), Charset.defaultCharset()));
        assertThrows(AppIntegrationException.class, () -> faultyResource.getContentAsInputStream());
    }

    @Test
    void getContentAsReader() throws IOException {
        assertEquals("Hello World!", IOUtils.toString(resource.getContentAsReader()));
        assertThrows(AppIntegrationException.class, () -> faultyResource.getContentAsReader());
    }

    @Test
    void setContentAsString() throws IOException {
        resource.setContent("new content");
        assertEquals("new content", IOUtils.toString(resource.getContentAsReader()));
    }

    @Test
    void setContentAsInputStream() throws IOException {
        resource.setContent(new ByteArrayInputStream("new content".getBytes()));
        assertEquals("new content", IOUtils.toString(resource.getContentAsReader()));
    }

    @Test
    void setContentAsReader() throws IOException {
        resource.setContent(new StringReader("new content"));
        assertEquals("new content", IOUtils.toString(resource.getContentAsReader()));
    }

    @Test
    void setContentSupplier() throws IOException {
        resource.setContentSupplier(() -> new ByteArrayInputStream("new content".getBytes()), InputStream.class);
        assertEquals("new content", IOUtils.toString(resource.getContentAsReader()));
    }

    @Test
    void appendInputStreamFilter() throws IOException {
        resource.setContent("ABC abc");
        resource.appendInputStreamFilter(ByteIncrementingInputStreamFilter::new);
        assertEquals("BCD!bcd", IOUtils.toString(resource.getContentAsReader()));
    }

    @Test
    void getContentAsParsedObject() throws IOException {
        assertEquals("Hello World!", resource.getContentAsParsedObject(String.class));
    }

    @Test
    void getCharset() {
        assertEquals(StandardCharsets.UTF_8, resource.getCharset());
    }

    @Test
    void addReference() throws URISyntaxException {
        resource.addReference("more.txt");
        resource.addReference("folder/even-more.txt", ExternalResourceType.TEXT);

        final List<ExternalResourceRef> refList = resource.getReferencedResources();
        assertEquals(2, refList.size());
        assertEquals(new URI("http://www.example.com/data/more.txt"), refList.get(0).getUri());
        assertEquals(ExternalResourceType.ANY, refList.get(0).getExpectedType());
        assertEquals(new URI("http://www.example.com/data/folder/even-more.txt"), refList.get(1).getUri());
        assertEquals(ExternalResourceType.TEXT, refList.get(1).getExpectedType());
    }

    @Test
    void setType() {
        assertEquals(ExternalResourceType.ANY, resource.getType());
        resource.setType(ExternalResourceType.TEXT);
        assertEquals(ExternalResourceType.TEXT, resource.getType());
        resource.setType(ExternalResourceType.CSS);
        assertEquals(ExternalResourceType.CSS, resource.getType());

        // set less-specific type
        resource.setType(ExternalResourceType.TEXT);
        assertEquals(ExternalResourceType.CSS, resource.getType());

        // set type, that is different than before (shall be ignored)
        resource.setType(ExternalResourceType.JAVASCRIPT);
        assertEquals(ExternalResourceType.CSS, resource.getType());
    }

    @Test
    void setLoadStatus() {
        resource.setLoadStatus(ExternalResource.LoadStatus.LOADED, Collections.singletonMap("size", "100kb"));
        assertEquals(ExternalResource.LoadStatus.LOADED, resource.getLoadStatus());
        assertEquals(1, resource.getLoadStatusDetails().size());
        assertEquals("100kb", resource.getLoadStatusDetails().get("size"));
    }


    private class FaultyStringConverter extends AbstractTextParser<String> {

        public FaultyStringConverter() {
            super(String.class);
        }

        @Override
        protected String serializeType(@Nonnull String source) throws IOException {
            throw new IOException("I'm faulty!");
        }

        @Override
        public Object parse(@Nonnull Reader reader) throws IOException {
            throw new IOException("I'm faulty!");
        }
    }

    private class ByteIncrementingInputStreamFilter extends FilterInputStream {

        public ByteIncrementingInputStreamFilter(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            return super.read() + 1;
        }

        @Override
        public int read(@Nonnull byte[] b) throws IOException {
            int size = super.read(b);
            for (int i = 0; i < b.length; i++) {
                b[i]++;
            }
            return size;
        }

        @Override
        public int read(@Nonnull byte[] b, int off, int len) throws IOException {
            int size = super.read(b, off, len);
            for (int i = 0; i < b.length; i++) {
                b[i]++;
            }
            return size;
        }
    }
}