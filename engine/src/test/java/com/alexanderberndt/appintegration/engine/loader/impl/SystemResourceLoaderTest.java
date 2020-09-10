package com.alexanderberndt.appintegration.engine.loader.impl;

import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SystemResourceLoaderTest {

    public static final String RESOURCE_URL = "classpath://system/simple-app1/server/resources/text1.txt";

    private final SystemResourceLoader resourceLoader = new SystemResourceLoader();

    @Test
    @Disabled
    void pretest() throws URISyntaxException {
        // Test the existence of the file itself
        assertNotNull(ClassLoader.getSystemResourceAsStream(new URI(RESOURCE_URL).getPath()));
    }

    @Test
    void load() {
        //resourceLoader.load()
    }


    @ParameterizedTest
    @CsvSource({
            "classpath://system/simple-app1/server/application-info.json,resources/text1.txt",
            "/simple-app1/server/application-info.json,resources/text1.txt",
            "classpath://system/any-app/server/application-info.json,/simple-app1/server/resources/text1.txt",
            "/any-app/server/application-info.json,/simple-app1/server/resources/text1.txt"
    })
    void resolveRelativeUrl(String baseUrl, String relativeUrl) throws URISyntaxException {

        URI defaultUri = new URI("classpath", "system", null, null);

        URI baseUri = defaultUri.resolve(new URI(baseUrl));

        ExternalResource baseResourceMock = Mockito.mock(ExternalResource.class);
        Mockito.when(baseResourceMock.getUri()).thenReturn(baseUri);

//        ExternalResourceRef ref = resourceLoader.resolveRelativeUrl(baseResourceMock, relativeUrl);
//        assertNotNull(ref);
//        assertEquals(new URI(RESOURCE_PATH), ref.getUri());

        System.out.println(baseUri);

        URI uri2 = baseUri.resolve(relativeUrl);
        assertEquals(new URI(RESOURCE_URL), uri2);
    }
}
