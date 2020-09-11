package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SystemResourceLoaderTest {

    public static final String RESOURCE_PATH = "/simple-app1/server/resources/text1.txt";

    private final SystemResourceLoader resourceLoader = new SystemResourceLoader();

    @Test
    void pretest() {
        // Test the existence of the file itself
        assertNotNull(ClassLoader.getSystemResourceAsStream(StringUtils.removeStart(RESOURCE_PATH, "/")));
    }

    @Test
    void load() throws URISyntaxException, ResourceLoaderException, IOException {
        final URI baseUri = resourceLoader.resolveBaseUri(RESOURCE_PATH);

        ExternalResourceRef ref = ExternalResourceRef.create(baseUri.toString(), ExternalResourceType.TEXT);
        ExternalResource resource = resourceLoader.load(ref, this::createExternalResource);
        assertEquals("Hello World!\n" +
                "This is a simple text file for testing.", resource.getContentAsParsedObject(String.class));
    }

@Test
    void notFound() throws URISyntaxException {
        final URI baseUri = resourceLoader.resolveBaseUri("/this/doesnt/exist");
        ExternalResourceRef ref = ExternalResourceRef.create(baseUri.toString(), ExternalResourceType.TEXT);
        assertThrows(ResourceLoaderException.class, () -> resourceLoader.load(ref, this::createExternalResource));
    }


    @ParameterizedTest
    @CsvSource({
            "classpath://system/simple-app1/server/application-info.json,resources/text1.txt",
            "/simple-app1/server/application-info.json,resources/text1.txt",
            "/simple-app1/server/application-info.json,/simple-app1/server/resources/text1.txt",
            "classpath://system/any-app/server/application-info.json,/simple-app1/server/resources/text1.txt",
            "/any-app/server/application-info.json,/simple-app1/server/resources/text1.txt"
    })
    void resolveRelativeUrl(String baseUrl, String relativeUrl) throws URISyntaxException {

        final URI baseUri = resourceLoader.resolveBaseUri(baseUrl);

        ExternalResource baseResourceMock = Mockito.mock(ExternalResource.class);
        Mockito.when(baseResourceMock.getUri()).thenReturn(baseUri);

        assertEquals(resourceLoader.resolveBaseUri(RESOURCE_PATH), baseUri.resolve(relativeUrl));
    }

    @Nonnull
    protected ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, Map<String, Object> metadataMap) {
        return new ExternalResource(uri, type, content, metadataMap, () -> Collections.singletonList(new StringConverter()));
    }

}
