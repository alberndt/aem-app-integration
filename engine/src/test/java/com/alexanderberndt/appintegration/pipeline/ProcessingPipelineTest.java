package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.core.CoreTestGlobalContext;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import com.alexanderberndt.appintegration.tasks.load.DownloadTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProcessingPipelineTest {

    protected ExternalResource createExternalResource(InputStream inputStream, ExternalResourceRef resourceRef, ResourceLoader loader) {
        return new ExternalResource(loader, resourceRef, () -> Collections.singletonList(new StringConverter()));
    }

    @Test
    void simpleProcessingPipeline() throws IOException, URISyntaxException {

        CoreTestGlobalContext context = new CoreTestGlobalContext(new Slf4jLogAppender());
        context.setResourceLoader(new SystemResourceLoader());
        ResourceLogger resourceLogger = context.getIntegrationLog().createResourceLogger("simple-processing-pipeline");

        ProcessingPipeline pipeline = ProcessingPipeline.createPipelineInstance(context, resourceLogger)
                .addTask(new PropertiesTask())
                .withTaskParam("random-input.length", 2000)
                .withTaskParam("something.else", true)
                .addTask(new DownloadTask())
                .addTask(new AddReferencedResourceTask(), "addReference1")
                .withTaskParam("relativeUrl", "resources/text1.txt")
                .withTaskParam("expectedType", "TEXT")
                .addTask(new AddReferencedResourceTask(), "addReference2")
                .withTaskParam("relativeUrl", "resources/to-be-filtered1.txt")
                .withTaskParam("expectedType", "TEXT")
                .build();

        ExternalResourceRef resourceRef = ExternalResourceRef.create("simple-app1/server/application-info.json", ExternalResourceType.APPLICATION_PROPERTIES);
        ExternalResource resource = pipeline.loadAndProcessResourceRef(resourceRef, this::createExternalResource);

        assertNotNull(resource);
        assertNotNull(resource.getReferencedResources());
        assertEquals(2, resource.getReferencedResources().size());
        assertEquals(new URI("simple-app1/server/resources/text1.txt"), resource.getReferencedResources().get(0).getUri());
        assertEquals(new URI("simple-app1/server/resources/to-be-filtered1.txt"), resource.getReferencedResources().get(1).getUri());
    }
}