package com.alexanderberndt.appintegration.api;

import org.junit.Test;

import static org.junit.Assert.*;

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