package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppInstance;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.engine.testsupport.TestExternalResourceCache;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.CORE_CONTEXT_PROVIDERS;
import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.*;

class AbstractAppIntegrationEngineTest {

    public static final String APPLICATION_ID = "test-app";

    public static final String TEST_APP_URL = "simple-app1/server/application-info.json";

    private TestAppIntegrationEngine engine;

    private TestAppInstance instance1;
    private TestAppInstance instance2;

    @BeforeEach
    void before() {

        engine = new TestAppIntegrationEngine();
        engine.getFactory().registerApplication(new TestApplication(APPLICATION_ID, TEST_APP_URL, SYSTEM_RESOURCE_LOADER_NAME, "simple-pipeline1", CORE_CONTEXT_PROVIDERS, null));


        Map<String, String> instanceContextMap = new HashMap<>();
        instanceContextMap.put("hello", "world");
        instanceContextMap.put("language", "de");
        instanceContextMap.put("newsletterId", "product-news");
        instance1 = new TestAppInstance(APPLICATION_ID, "subscribe", instanceContextMap);
        instanceContextMap.put("language", "en");
        instance2 = new TestAppInstance(APPLICATION_ID, "subscribe", instanceContextMap);
    }

    @Test
    void pretest() throws URISyntaxException, IOException, ResourceLoaderException {
        ResourceLoader loader = engine.getFactory().getResourceLoader(SYSTEM_RESOURCE_LOADER_NAME);
        assertNotNull(loader);

        URI appInfoUri = loader.resolveBaseUri(TEST_APP_URL);
        assertNotNull(appInfoUri);

        ExternalResource appInfoResource = loader.load(new ExternalResourceRef(appInfoUri, ExternalResourceType.APPLICATION_PROPERTIES), engine.getFactory().getExternalResourceFactory());
        assertNotNull(appInfoResource);

        String content = appInfoResource.getContentAsParsedObject(String.class);
        assertTrue(StringUtils.isNotBlank(content));
    }

//    @Test
//    void loadApplicationInfoJson() throws IOException {
//        ApplicationInfoJson applicationInfo = engine.loadApplicationInfoJson(verifiedInstance.getApplication());
//
//        assertNotNull(applicationInfo);
//        assertEquals("Newsletter", applicationInfo.getName());
//    }

    @Test
    void prefetch() throws URISyntaxException {

        engine.prefetch(Arrays.asList(instance1, instance2));

        final TestExternalResourceCache cache = engine.getExternalResourceCache(APPLICATION_ID);
        final List<URI> keyList = cache.getCacheKeys();
        assertEquals(5, keyList.size());
        assertEquals(new URI("classpath://system/simple-app1/server/application-info.json"), keyList.get(0));
        assertEquals(new URI("classpath://system/simple-app1/server/subscribe.product-news.de.html"), keyList.get(1));
        assertEquals(new URI("classpath://system/simple-app1/server/subscribe.product-news.en.html"), keyList.get(2));
        assertEquals(new URI("classpath://system/simple-app1/server/js/registration.js"), keyList.get(3));
        assertEquals(new URI("classpath://system/simple-app1/server/css/style.css"), keyList.get(4));
    }

    @Test
    void getHtmlSnippet() throws IOException, URISyntaxException {
        ExternalResource htmlSnippet = engine.getHtmlSnippet(instance1);
        assertNotNull(htmlSnippet);

        final String content = htmlSnippet.getContentAsParsedObject(String.class);
        assertTrue(StringUtils.isNotBlank(content));
        assertNotEquals("Fake content!", content);
        final String strippedContent = content.trim().replaceAll(">[\\s\\r\\n]*<", "><");
        assertTrue(strippedContent.startsWith("<div><h1>Abonniere die Product News</h1>"), strippedContent);

        final TestExternalResourceCache cache = engine.getExternalResourceCache(APPLICATION_ID);
        final List<URI> keyList = cache.getCacheKeys();

        assertEquals(2, keyList.size());
        assertEquals(new URI("classpath://system/simple-app1/server/application-info.json"), keyList.get(0));

        final URI snippetURI = new URI("classpath://system/simple-app1/server/subscribe.product-news.de.html");
        assertEquals(snippetURI, keyList.get(1));

        // modify cache
        cache.storeResource(new ExternalResource(snippetURI, ExternalResourceType.HTML_SNIPPET, new ByteArrayInputStream("Fake content!".getBytes()), null, null));
        ExternalResource htmlSnippet2 = engine.getHtmlSnippet(instance1);
        assertNotNull(htmlSnippet2);
        final String content2 = htmlSnippet2.getContentAsParsedObject(String.class);
        assertEquals("Fake content!", content2);
    }

}