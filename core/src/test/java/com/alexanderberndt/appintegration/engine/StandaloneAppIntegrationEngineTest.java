package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.standalone.StandaloneAppIntegrationEngine;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StandaloneAppIntegrationEngineTest {

    @Test
    void loadHtmlSnippet() throws IOException {

        Application newsletterApp = mock(Application.class);
        when(newsletterApp.getApplicationInfoUrl()).thenReturn("simple-app1/server/application-info.json");
        when(newsletterApp.getUsedResourceLoader()).thenReturn("classloader");
        when(newsletterApp.getUsedContextProviders()).thenReturn(Arrays.asList("test", "properties"));

        StandaloneAppIntegrationEngine appIntegrationEngine = new StandaloneAppIntegrationEngine();
        appIntegrationEngine.registerApplication("newsletter", newsletterApp);

        String htmlSnippet = appIntegrationEngine.loadHtmlSnippet("newsletter", "subscribe",
                "simple-app1/local/subscribe-instance1.properties");

        assertNotNull(htmlSnippet);
        System.out.println(htmlSnippet);

    }
}