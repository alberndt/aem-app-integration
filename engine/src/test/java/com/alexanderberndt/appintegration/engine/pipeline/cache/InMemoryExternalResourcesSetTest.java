package com.alexanderberndt.appintegration.engine.pipeline.cache;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourcesSet;
import com.alexanderberndt.appintegration.tasks.cache.InMemoryExternalResourcesSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryExternalResourcesSetTest {


    private ExternalResourcesSet resourcesSet;

    private final static String TEST_DATA = "Hello World!\n" +
            "This is a simple text file for testing.";

    @BeforeEach
    void setup() {
        resourcesSet = new InMemoryExternalResourcesSet();
        resourcesSet.addResourceReference("resources/text1.txt", ExternalResourceType.PLAIN_TEXT);
    }


    @Test
    void pretest() {
        // Test the existence of the file itself
        assertNotNull(ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/text1.txt"));
    }


}