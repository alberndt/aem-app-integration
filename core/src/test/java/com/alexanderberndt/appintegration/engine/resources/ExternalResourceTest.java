package com.alexanderberndt.appintegration.engine.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalResourceTest {

    public static final String TEST_DATA = "Hello World,\n"
            + "with some new lines.\n"
            + "Thank you!";

    public static final String TEST_DATA2 = "This is\n"
            + "something else!\n"
            + "Hopefully, your ...!";

    private ExternalResource resource;

    @BeforeEach
    void beforeEach() {
        resource = new ExternalResource(null, new ExternalResourceRef("test-data.txt"));
        resource.setCharset(StandardCharsets.UTF_8);
        resource.setContent(new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void setProperty() {
    }

    @Test
    void getProperty() {
    }

    @Test
    void getInputStream() throws IOException {

    }

    @Test
    void getBytes() {
    }

    @Test
    void getCharset() {
    }

    @Test
    void getReader() {
    }

    @Test
    void getString() throws IOException {
        assertEquals(TEST_DATA, resource.getContentAsString());
        assertEquals(TEST_DATA, resource.getContentAsString());
        assertEquals(TEST_DATA, resource.getContentAsString());
    }

    @Test
    void addReference() {
    }

    @Test
    void getReferencedResources() {
    }

    @Test
    void readFully() {
    }

    @Test
    void getUrl() {
    }

    @Test
    void setRelativeUrlResolver() {
    }

    @Test
    void setUrl() {
    }

    @Test
    void getType() {
    }

    @Test
    void setType() {
    }

    @Test
    void setCharset() {
    }

    @Test
    void setInputStream() {
    }

    @Test
    void setReader() {
    }
}