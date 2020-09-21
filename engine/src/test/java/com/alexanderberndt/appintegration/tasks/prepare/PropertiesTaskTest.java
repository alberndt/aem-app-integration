package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.testsupport.TestApplication;
import com.alexanderberndt.appintegration.engine.testsupport.TestLoadingTask;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.simple.SimplePipelineBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory.SYSTEM_RESOURCE_LOADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PropertiesTaskTest {

    private static final String TEST_APP = "test-app";

    private AppIntegrationEngine engine;

    private TestValue<String> stringValue;

    private TestValue<Integer> numberValue;


    @BeforeEach
    void beforeEach() {
        final TestApplication testApplication = new TestApplication(TEST_APP, "xxx", SYSTEM_RESOURCE_LOADER_NAME, "custom", Collections.emptyList(), null);

        final ProcessingPipeline pipeline = new SimplePipelineBuilder()
                .addPreparationTask("check-type", new ResourceTypeByFileExtensionTask())
                .addPreparationTask("properties", new PropertiesTask())
                .withTaskParam("verify:hello", "Hello World!")
                .withTaskParam("verify:hello.css", "This is a CSS value!")
                .withTaskParam("verify:the-number", 42)
                .addLoadingTask("load", new TestLoadingTask("Hello World!"))
                .addProcessingTask("verify", (context, resource) -> {
                    stringValue.value = context.getValue("hello", String.class);
                    numberValue.value = context.getValue("the-number", Integer.class);
                })
                .build();

        final TestAppIntegrationFactory factory = new TestAppIntegrationFactory();
        factory.registerApplication(testApplication);
        factory.registerPipeline("custom", pipeline);

        engine = new TestAppIntegrationEngine(factory, Slf4jLogAppender::new);

        stringValue = new TestValue<>();
        numberValue = new TestValue<>();
    }



    @Test
    void prepareTextRes() {
        engine.getStaticResource("test-app", "/test.txt");
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