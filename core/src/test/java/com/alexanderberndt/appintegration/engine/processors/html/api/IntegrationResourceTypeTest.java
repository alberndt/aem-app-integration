package com.alexanderberndt.appintegration.engine.processors.html.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationResourceTypeTest {

    @Test
    public void testParse() {
        assertEquals(IntegrationResourceType.HTML, IntegrationResourceType.parse("html"));
        assertEquals(IntegrationResourceType.HTML_SNIPPET, IntegrationResourceType.parse("html-snippet"));
        assertEquals(IntegrationResourceType.JAVASCRIPT, IntegrationResourceType.parse("javascript"));
        assertEquals(IntegrationResourceType.CSS, IntegrationResourceType.parse("css"));
        assertEquals(IntegrationResourceType.CACHE_MANIFEST, IntegrationResourceType.parse("cache-manifest"));
    }

}