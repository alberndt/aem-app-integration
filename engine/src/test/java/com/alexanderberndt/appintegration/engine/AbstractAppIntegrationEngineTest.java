package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppInstance;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.CORE_CONTEXT_PROVIDERS;
import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractAppIntegrationEngineTest {

    public static final String TEST_APP_URL = "simple-app1/server/application-info.json";

    private TestAppIntegrationEngine engine;

    private TestAppInstance instance1;
    private TestAppInstance instance2;

    @BeforeEach
    void before() {

        engine = new TestAppIntegrationEngine();
        engine.getFactory().registerApplication(new TestApplication("test-app", TEST_APP_URL, SYSTEM_RESOURCE_LOADER_NAME, "simple-pipeline1", CORE_CONTEXT_PROVIDERS, null));


        Map<String, String> instanceContextMap = new HashMap<>();
        instanceContextMap.put("hello", "world");
        instanceContextMap.put("language", "de");
        instanceContextMap.put("newsletterId", "product-news");
        instance1 = new TestAppInstance("test-app", "subscribe", instanceContextMap);
        instanceContextMap.put("language", "en");
        instance2 = new TestAppInstance("test-app", "subscribe", instanceContextMap);
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
    void prefetch()  {
        engine.prefetch(Arrays.asList(instance1, instance2));
    }

    @Test
    void getHtmlSnippet() throws IOException {
        ExternalResource htmlSnippet = engine.getHtmlSnippet(instance1);
        assertNotNull(htmlSnippet);

        final String content = htmlSnippet.getContentAsParsedObject(String.class);
        assertTrue(StringUtils.isNotBlank(content));
    }

}