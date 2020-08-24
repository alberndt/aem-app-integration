package com.alexanderberndt.appintegration.engine.resources;

import org.junit.jupiter.api.Test;

import static com.alexanderberndt.appintegration.engine.resources.ExternalResourceType.*;
import static org.junit.jupiter.api.Assertions.*;

class ExternalResourceTypeTest {

    @Test
    void getDefaultCharset() {
    }

    @Test
    void isHtmlDocument() {
    }

    @Test
    void getLessQualifiedType() {
        assertEquals(TEXT, HTML.getLessQualifiedType());
        assertEquals(ANY, BINARY.getLessQualifiedType());
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
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}