package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpResourceLoaderTest {


    private final ResourceLoader resourceLoader = new HttpResourceLoader();

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.stubFor(get(urlEqualTo("/test-url"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Encoding", "utf-8")
                        .withBody("Hello World!")));

        wireMockServer.stubFor(get(urlEqualTo("/not-found"))
                .willReturn(aResponse()
                        .withStatus(404)));

        wireMockServer.stubFor(get(urlEqualTo("/test-url"))
                .withHeader("If-None-Match", matching("12345"))
                .willReturn(aResponse()
                        .withStatus(304)));

        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void load() throws IOException, ResourceLoaderException {
        ExternalResourceRef ref = ExternalResourceRef.create("http://localhost:8089/test-url", ExternalResourceType.TEXT);
        ExternalResource resource = resourceLoader.load(ref, this::createExternalResource);
        assertEquals("Hello World!", resource.getContentAsParsedObject(String.class));
    }

    @Test
    void loadCached() throws IOException, ResourceLoaderException, URISyntaxException {
        ExternalResource cachedRes = createExternalResource(new URI("http://localhost:8089/test-url"), ExternalResourceType.TEXT,
                new ByteArrayInputStream("Cached data".getBytes()), null);

        ExternalResourceRef ref = ExternalResourceRef.create("http://localhost:8089/test-url", ExternalResourceType.TEXT);
        ref.setCachedExternalRes(cachedRes);
        ref.setMetadata("HttpHeader.ETag", "12345");


        ExternalResource resource = resourceLoader.load(ref, this::createExternalResource);
        assertEquals("Cached data", resource.getContentAsParsedObject(String.class));
    }

    @Test
    void notFound() {
        ExternalResourceRef ref = ExternalResourceRef.create("http://localhost:8089/not-found", ExternalResourceType.TEXT);
        assertThrows(AppIntegrationException.class, () -> resourceLoader.load(ref, this::createExternalResource));
    }


    @Nonnull
    protected ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, Map<String, Object> metadataMap) {
        return new ExternalResource(uri, type, content, metadataMap, () -> Collections.singletonList(new StringConverter()));
    }


}