package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.engine.testsupport.TestLoadingTask;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.simple.SimplePipelineBuilder;
import com.alexanderberndt.appintegration.tasks.prepare.ResourceTypeByFileExtensionTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TaskContextTest2 {

    private static final String TEST_APP = "test-app";

    private TestAppIntegrationEngine engine;

    private TestValue<String> stringValue;

    private TestValue<Integer> numberValue;


    @BeforeEach
    void beforeEach() {

        final TestApplication testApplication = new TestApplication(TEST_APP, "xxx", SYSTEM_RESOURCE_LOADER_NAME, "custom", Collections.emptyList(), null);

        final ProcessingPipeline pipeline = new SimplePipelineBuilder()
                .addPreparationTask("check-type", new ResourceTypeByFileExtensionTask())
                .addLoadingTask("load", new TestLoadingTask("Hello World!"))
                .addProcessingTask("verify", (context, resource) -> {
                    stringValue.value = context.getValue("str", String.class);
                    numberValue.value = context.getValue("number", Integer.class);
                })
                .withTaskParam("str", "Hello World!")
                .withTaskParam("str.css", "This is a CSS value!")
                .withTaskParam("number", 42)
                .build();


        engine = new TestAppIntegrationEngine();
        engine.getFactory().registerApplication(testApplication);
        engine.getFactory().registerPipeline("custom", pipeline);

        stringValue = new TestValue<>();
        numberValue = new TestValue<>();
    }


    @Test
    void testTextRes() {
        final ExternalResourceRef resourceRef = ExternalResourceRef.create("/test.txt", ExternalResourceType.TEXT);
        engine.getStaticResource(TEST_APP, "/test.txt");
        assertEquals("Hello World!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }

    @Test
    void prepareCssRes() {
        final ExternalResourceRef resourceRef = ExternalResourceRef.create("/test.css", ExternalResourceType.CSS);
        engine.getStaticResource(TEST_APP, "/test.css");
        assertEquals("This is a CSS value!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }


    private static class TestValue<T> {
        T value;
    }
}