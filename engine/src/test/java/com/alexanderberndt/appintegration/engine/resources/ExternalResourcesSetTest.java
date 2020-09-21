package com.alexanderberndt.appintegration.engine.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

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

        Iterator<ExternalResourceRef> iterator = externalResourcesSet.iterator();

        assertTrue(iterator.hasNext());
        ExternalResourceRef ref1 = iterator.next();
        assertEquals(imageUri, ref1.getUri());
        assertEquals(ExternalResourceType.BINARY, ref1.getExpectedType());


        assertTrue(iterator.hasNext());
        ExternalResourceRef ref2 = iterator.next();
        assertEquals(cssUri, ref2.getUri());
        assertEquals(ExternalResourceType.CSS, ref2.getExpectedType());

        assertFalse(iterator.hasNext());
    }

    @Test
    void equals() {
        externalResourcesSet.add(new ExternalResourceRef(imageUri, ExternalResourceType.BINARY));
        externalResourcesSet.add(new ExternalResourceRef(cssUri, ExternalResourceType.CSS));

        final ExternalResourcesSet externalResourcesSet2 = new ExternalResourcesSet();
        externalResourcesSet2.add(new ExternalResourceRef(imageUri, ExternalResourceType.ANY));
        externalResourcesSet2.add(new ExternalResourceRef(cssUri, ExternalResourceType.TEXT));

        assertEquals(externalResourcesSet.hashCode(), externalResourcesSet2.hashCode());
        assertEquals(externalResourcesSet2, externalResourcesSet);
        assertEquals(externalResourcesSet, externalResourcesSet2);
    }
}