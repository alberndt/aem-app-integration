package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.logging.appender.JsonLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppInstance;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.CORE_CONTEXT_PROVIDERS;
import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.*;

class AppIntegrationEngineTest {

    public static final String TEST_APP_URL = "simple-app1/server/application-info.json";

    private TestAppIntegrationFactory factory;

    private TestAppIntegrationEngine engine;

    private TestAppInstance instance1;
    private TestAppInstance instance2;

    private VerifiedInstance<TestAppInstance> verifiedInstance;

    @BeforeEach
    void before() {
        factory = new TestAppIntegrationFactory();
        factory.registerApplication("test-app", new TestApplication(TEST_APP_URL, SYSTEM_RESOURCE_LOADER_NAME, "simple-pipeline1", CORE_CONTEXT_PROVIDERS, null));
        engine = new TestAppIntegrationEngine(factory, () -> new JsonLogAppender(() -> new FileWriter("../logviewer/public/test-app-log.json")));
        //engine = new TestAppIntegrationEngine(factory, Slf4jLogAppender::new);

        Map<String, String> instanceContextMap = new HashMap<>();
        instanceContextMap.put("hello", "world");
        instanceContextMap.put("language", "de");
        instanceContextMap.put("newsletterId", "product-news");
        instance1 = new TestAppInstance("test-app", "subscribe", instanceContextMap);
        instanceContextMap.put("language", "en");
        instance2 = new TestAppInstance("test-app", "subscribe", instanceContextMap);
        verifiedInstance = VerifiedInstance.verify(instance1, factory);
        assertNotNull(verifiedInstance);
    }

    @Test
    @Disabled
    void loadApplicationInfoJson() throws IOException {
        ApplicationInfoJson applicationInfo = engine.loadApplicationInfoJson(verifiedInstance.getApplication());

        assertNotNull(applicationInfo);
        assertEquals("Newsletter", applicationInfo.getName());
    }

    @Test
    @Disabled
    void prefetch() throws IOException {
//        engine.prefetch(logger, context, applicationId, Arrays.asList(instance1, instance2));
    }

    @Test
    @Disabled
    void getHtmlSnippet() throws IOException {
        TestAppInstance instance = new TestAppInstance("test-app", "hello");
        ExternalResource htmlSnippet = engine.getHtmlSnippet(instance);

        assertNotNull(htmlSnippet);
    }

    @Test
    @Disabled
    void resolveStringWithContextVariables() {
        assertEquals("Hello world from de", engine.resolveStringWithContextVariables(verifiedInstance, "Hello ${hello} from ${language}"));
        assertThrows(AppIntegrationException.class, () -> engine.resolveStringWithContextVariables(verifiedInstance, "Hello ${hello} from ${language} with ${something-unknown}"));
    }

    @Test
    @Disabled
    void resolveSnippetResource() throws IOException, URISyntaxException {
        ApplicationInfoJson applicationInfoJson = engine.loadApplicationInfoJson(verifiedInstance.getApplication());

        final ExternalResourceRef resourceRef = engine.resolveSnippetResource(verifiedInstance, applicationInfoJson);

        assertNotNull(resourceRef);
        assertEquals(ExternalResourceType.HTML_SNIPPET, resourceRef.getExpectedType());
        assertEquals(new URI("simple-app1/server/subscribe.product-news.de.html"), resourceRef.getUri());
    }


}