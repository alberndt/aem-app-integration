package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.engine.testsupport.TestGlobalContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProcessingPipelineTest {

    @Test
    void simpleLoadTest() throws URISyntaxException, IOException {
        TestAppIntegrationFactory factory = new TestAppIntegrationFactory();
        factory.registerApplication(new TestApplication("test-app", "xxx", SYSTEM_RESOURCE_LOADER_NAME, "xxx", Collections.emptyList(), null));

        TestGlobalContext context = new TestGlobalContext("test-app", factory, null, null);
        ProcessingPipeline pipeline = new ProcessingPipeline(Collections.emptyList(), Collections.emptyList());

        URI uri = context.getResourceLoader().resolveBaseUri("com/alexanderberndt/appintegration/pipeline/simple-text.txt");
        ExternalResourceRef resourceRef = new ExternalResourceRef(uri, ExternalResourceType.TEXT);
        ExternalResource resource = pipeline.loadAndProcessResourceRef(context, resourceRef);

        assertNotNull(resource);
        assertEquals("Hello world!", resource.getContentAsParsedObject(String.class));
    }

}