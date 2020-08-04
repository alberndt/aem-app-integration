package com.alexanderberndt.appintegration.engine.pipeline.cache;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourcesSet;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.SystemResourceLoader;
import com.alexanderberndt.appintegration.tasks.cache.InMemoryExternalResourcesSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryExternalResourcesSetTest {

    private ResourceLoader resourceLoader;

    private ExternalResourcesSet resourcesSet;

    private final static String TEST_DATA = "Hello World!\n" +
            "This is a simple text file for testing.";

    @BeforeEach
    void setup() {
        resourceLoader = new SystemResourceLoader();
        resourcesSet = new InMemoryExternalResourcesSet(resourceLoader, "simple-app1/server/application-info.json");

        resourcesSet.addResourceReference("resources/text1.txt", ExternalResourceType.PLAIN_TEXT);
    }


    @Test
    void pretest() {
        // Test the existence of the file itself
        assertNotNull(ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/text1.txt"));
    }

    @Test
    void prefetch() throws IOException {
        resourcesSet.prefetchAll();
        ExternalResource text1Res = resourcesSet.getResource("resources/text1.txt");

        assertNotNull(text1Res);
        assertNotNull(text1Res.getContentAsInputStream());
        assertEquals(TEST_DATA, text1Res.getContentAsString());
    }

    @Test
    void getResource() throws IOException {
        ExternalResource text1Res = resourcesSet.getResource("resources/text1.txt");

        assertNotNull(text1Res);
        assertNotNull(text1Res.getContentAsInputStream());
        assertEquals(TEST_DATA, text1Res.getContentAsString());
    }
}