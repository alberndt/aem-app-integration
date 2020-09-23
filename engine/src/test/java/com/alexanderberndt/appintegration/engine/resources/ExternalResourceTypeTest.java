package com.alexanderberndt.appintegration.engine.resources;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.alexanderberndt.appintegration.engine.resources.ExternalResourceType.*;
import static org.junit.jupiter.api.Assertions.*;

class ExternalResourceTypeTest {

    @Test
    void getDefaultCharset() {
        assertEquals(StandardCharsets.UTF_8, TEXT.getDefaultCharset());
    }

    @Test
    void isHtmlDocument() {
        assertTrue(HTML.isHtmlDocument());
        assertTrue(HTML_SNIPPET.isHtmlDocument());
        assertFalse(TEXT.isHtmlDocument());
        assertFalse(JAVASCRIPT.isHtmlDocument());
        assertFalse(CSS.isHtmlDocument());
    }

    @Test
    void getLessQualifiedType() {
        assertEquals(TEXT, HTML.getLessQualifiedType());
        assertEquals(ANY, BINARY.getLessQualifiedType());
    }

    @Test
    void isSpecializationOf() {
        assertTrue(JAVASCRIPT.isSpecializationOf(TEXT));
        assertTrue(CSS.isSpecializationOf(TEXT));
        assertTrue(CSS.isSpecializationOf(ANY));
        assertTrue(HTML_SNIPPET.isSpecializationOf(ANY));
        assertTrue(HTML_SNIPPET.isSpecializationOf(TEXT));

        assertFalse(JAVASCRIPT.isSpecializationOf(BINARY));
        assertFalse(TEXT.isSpecializationOf(JAVASCRIPT));
        assertFalse(TEXT.isSpecializationOf(CSS));
    }

    @Test
    void isSameOrSpecializationOf() {
        assertTrue(HTML.isSameOrSpecializationOf(TEXT));

        assertFalse(TEXT.isSameOrSpecializationOf(HTML));
        assertFalse(HTML.isSameOrSpecializationOf(BINARY));
    }

    @Test
    void isMoreQualifiedThan() {
        assertTrue(HTML.isMoreQualifiedThan(TEXT));
        assertTrue(BINARY.isMoreQualifiedThan(TEXT));
        assertTrue(BINARY.isMoreQualifiedThan(HTML));
    }

    @Test
    void parse() {
        assertEquals(ANY, ExternalResourceType.parse("any"));
        assertNull(ExternalResourceType.parse("unknown"));
        assertNull(ExternalResourceType.parse(null));
        assertNull(ExternalResourceType.parse(""));
    }

    @Test
    void testToString() {
        assertEquals("html-snippet", HTML_SNIPPET.toString());
        assertEquals("any", ANY.toString());
        assertEquals("cache-manifest", CACHE_MANIFEST.toString());
    }

}