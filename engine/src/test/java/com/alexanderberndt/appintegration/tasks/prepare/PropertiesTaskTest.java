package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.engine.testsupport.TestResourceLoader;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.simple.SimplePipelineBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PropertiesTaskTest {

    private static final String TEST_APP = "test-app";

    private TestResourceLoader testResourceLoader;

    private TestAppIntegrationEngine engine;

    private TestValue<String> stringValue;

    private TestValue<Integer> numberValue;


    @BeforeEach
    void beforeEach() {
        final TestApplication testApplication = new TestApplication(TEST_APP, "xxx", "test-loader", "custom", Collections.emptyList(), null);

        final ProcessingPipeline pipeline = new SimplePipelineBuilder()
                .addPreparationTask("check-type", new ResourceTypeByFileExtensionTask())
                .addPreparationTask("properties", new PropertiesTask())
                .withTaskParam("verify:hello", "Hello World!")
                .withTaskParam("verify:hello.css", "This is a CSS value!")
                .withTaskParam("verify:the-number", 42)
                .addProcessingTask("verify", (context, resource) -> {
                    stringValue.value = context.getValue("hello", String.class);
                    numberValue.value = context.getValue("the-number", Integer.class);
                })
                .build();

        final TestAppIntegrationFactory factory = new TestAppIntegrationFactory();

        factory.registerApplication(testApplication);
        factory.registerPipeline("custom", pipeline);
        testResourceLoader = new TestResourceLoader("Hello World!");
        factory.registerResourceLoader("test-loader", testResourceLoader);

        engine = new TestAppIntegrationEngine(factory, Slf4jLogAppender::new);

        stringValue = new TestValue<>();
        numberValue = new TestValue<>();
    }



    @Test
    void prepareTextRes() {
        ExternalResource res = engine.getStaticResource("test-app", "/test.txt");
        assertNotNull(res);
        assertEquals("Hello World!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }

    @Test
    void prepareCssRes() {
        engine.getStaticResource("test-app", "/test.css");
        assertEquals("This is a CSS value!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }


    private static class TestValue<T> {
        T value;
    }
}