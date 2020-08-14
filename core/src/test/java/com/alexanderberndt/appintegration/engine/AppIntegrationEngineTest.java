package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory;
import com.alexanderberndt.appintegration.core.CoreTestAppIntegrationEngine;
import com.alexanderberndt.appintegration.core.CoreTestApplicationInstance;
import com.alexanderberndt.appintegration.engine.resourcetypes.appinfo.ApplicationInfoJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory.CORE_CONTEXT_PROVIDERS;
import static com.alexanderberndt.appintegration.core.CoreAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppIntegrationEngineTest {

    public static final String TEST_APP_URL = "simple-app1/server/application-info.json";

    private CoreAppIntegrationFactory factory;

    private CoreTestAppIntegrationEngine engine;

    @BeforeEach
    void before() {
        factory = new CoreAppIntegrationFactory();
        factory.registerApplication("test-app", new Application(TEST_APP_URL, SYSTEM_RESOURCE_LOADER_NAME, null, CORE_CONTEXT_PROVIDERS));
        engine = new CoreTestAppIntegrationEngine(factory);
    }

    @Test
    void loadApplicationInfoJson() throws IOException {
        ApplicationInfoJson applicationInfo = engine.loadApplicationInfoJson("test-app");

        assertNotNull(applicationInfo);
        assertEquals("Newsletter", applicationInfo.getName());
    }

    @Test
    void getHtmlSnippet() throws IOException {
        CoreTestApplicationInstance instance = new CoreTestApplicationInstance("test-app", "hello");
        String htmlSnippet = engine.getHtmlSnippet(instance);

        assertNotNull(htmlSnippet);
    }

}