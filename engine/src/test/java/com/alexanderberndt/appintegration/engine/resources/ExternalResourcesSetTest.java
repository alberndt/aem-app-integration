package com.alexanderberndt.appintegration.engine.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ExternalResourcesSetTest {

    private URI imageUri;
    private URI cssUri;

    private ExternalResourcesSet externalResourcesSet;

    @BeforeEach
    void beforeEach() throws URISyntaxException {
        imageUri = new URI("http://www.example.com/image.jpg");
        cssUri = new URI("http://www.example.com/test.css");
        externalResourcesSet = new ExternalResourcesSet();
    }

    @Test
    void add() {
        externalResourcesSet.add(new ExternalResourceRef(imageUri, ExternalResourceType.ANY));
        assertEquals(1, externalResourcesSet.size());

        externalResourcesSet.add(new ExternalResourceRef(cssUri, ExternalResourceType.ANY));
        assertEquals(2, externalResourcesSet.size());

        externalResourcesSet.add(new ExternalResourceRef(imageUri, ExternalResourceType.BINARY));
        assertEquals(2, externalResourcesSet.size());

        externalResourcesSet.add(new ExternalResourceRef(cssUri, ExternalResourceType.CSS));
        assertEquals(2, externalResourcesSet.size());

        externalResourcesSet.add(new ExternalResourceRef(cssUri, ExternalResourceType.TEXT));
        assertEquals(2, externalResourcesSet.size());


        assertTrue(externalResourcesSet.hasMoreUnprocessed());
        ExternalResourceRef ref1 = externalResourcesSet.nextUnprocessed();
        assertEquals(imageUri, ref1.getUri());
        assertEquals(ExternalResourceType.BINARY, ref1.getExpectedType());


        assertTrue(externalResourcesSet.hasMoreUnprocessed());
        ExternalResourceRef ref2 = externalResourcesSet.nextUnprocessed();
        assertEquals(cssUri, ref2.getUri());
        assertEquals(ExternalResourceType.CSS, ref2.getExpectedType());

        assertFalse(externalResourcesSet.hasMoreUnprocessed());
    }
}