package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SystemResourceLoaderTest {

    public static final String RESOURCE_PATH = "simple-app1/server/resources/text1.txt";

    private SystemResourceLoader resourceLoader = new SystemResourceLoader();

    @Test
    void pretest() {
        // Test the existence of the file itself
        assertNotNull(ClassLoader.getSystemResourceAsStream(RESOURCE_PATH));
    }

    @Test
    void load() {
        //resourceLoader.load()
    }


    @ParameterizedTest
    @CsvSource({
            "simple-app1/server/application-info.json,resources/text1.txt",
            "/simple-app1/server/application-info.json,resources/text1.txt",
            "any-app/server/application-info.json,/simple-app1/server/resources/text1.txt",
            "/any-app/server/application-info.json,/simple-app1/server/resources/text1.txt"
    })
    void resolveRelativeUrl(String baseUrl, String relativeUrl) {
        String url = resourceLoader.resolveRelativeUrl(baseUrl, relativeUrl);
        assertNotNull(url);
        assertEquals(RESOURCE_PATH, url);
    }
}
