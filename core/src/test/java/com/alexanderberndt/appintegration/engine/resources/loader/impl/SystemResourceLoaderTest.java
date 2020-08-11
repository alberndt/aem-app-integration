package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        ExternalResource baseResourceMock = Mockito.mock(ExternalResource.class);
        Mockito.when(baseResourceMock.getUrl()).thenReturn(baseUrl);

        ExternalResourceRef ref = resourceLoader.resolveRelativeUrl(baseResourceMock, relativeUrl);
        assertNotNull(ref);
        assertEquals(RESOURCE_PATH, ref.getUrl());
    }
}
