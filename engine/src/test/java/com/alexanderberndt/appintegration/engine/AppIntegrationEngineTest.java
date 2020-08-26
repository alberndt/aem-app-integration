package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory;
import com.alexanderberndt.appintegration.core.CoreTestAppInstance;
import com.alexanderberndt.appintegration.core.CoreTestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.appender.JsonLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory.CORE_CONTEXT_PROVIDERS;
import static com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.*;

class AppIntegrationEngineTest {

    public static final String TEST_APP_URL = "simple-app1/server/application-info.json";

    private CoreAppIntegrationFactory factory;

    private CoreTestAppIntegrationEngine engine;

    private CoreTestAppInstance instance1;
    private CoreTestAppInstance instance2;

    private VerifiedInstance<CoreTestAppInstance> verifiedInstance;

    @BeforeEach
    void before() {
        factory = new CoreAppIntegrationFactory();
        factory.registerApplication("test-app", new Application(TEST_APP_URL, SYSTEM_RESOURCE_LOADER_NAME, "simple-pipeline1", CORE_CONTEXT_PROVIDERS, null));
        engine = new CoreTestAppIntegrationEngine(factory, () -> new JsonLogAppender(() -> new FileWriter("../logviewer/public/test-app-log.json")));

        Map<String, String> instanceContextMap = new HashMap<>();
        instanceContextMap.put("hello", "world");
        instanceContextMap.put("language", "de");
        instanceContextMap.put("newsletterId", "product-news");
        instance1 = new CoreTestAppInstance("test-app", "subscribe", instanceContextMap);
        instanceContextMap.put("language", "en");
        instance2 = new CoreTestAppInstance("test-app", "subscribe", instanceContextMap);
        verifiedInstance = VerifiedInstance.verify(instance1, factory);
        assertNotNull(verifiedInstance);
    }

    @Test
    void loadApplicationInfoJson() throws IOException {
        ApplicationInfoJson applicationInfo = engine.loadApplicationInfoJson(verifiedInstance.getApplication());

        assertNotNull(applicationInfo);
        assertEquals("Newsletter", applicationInfo.getName());
    }

    @Test
    void prefetch() throws IOException {
        engine.prefetch(Arrays.asList(instance1, instance2));
    }

    @Test
    @Disabled
    void getHtmlSnippet() throws IOException {
        CoreTestAppInstance instance = new CoreTestAppInstance("test-app", "hello");
        ExternalResource htmlSnippet = engine.getHtmlSnippet(instance);

        assertNotNull(htmlSnippet);
    }

    @Test
    void resolveStringWithContextVariables() {
        assertEquals("Hello world from de", engine.resolveStringWithContextVariables(verifiedInstance, "Hello ${hello} from ${language}"));
        assertThrows(AppIntegrationException.class, () -> engine.resolveStringWithContextVariables(verifiedInstance, "Hello ${hello} from ${language} with ${something-unknown}"));
    }

    @Test
    void resolveSnippetResource() throws IOException {
        ApplicationInfoJson applicationInfoJson = engine.loadApplicationInfoJson(verifiedInstance.getApplication());

        final ExternalResourceRef resourceRef = engine.resolveSnippetResource(verifiedInstance, applicationInfoJson);

        assertNotNull(resourceRef);
        assertEquals(ExternalResourceType.HTML_SNIPPET, resourceRef.getExpectedType());
        assertEquals("simple-app1/server/subscribe.product-news.de.html", resourceRef.getUrl());
    }


}