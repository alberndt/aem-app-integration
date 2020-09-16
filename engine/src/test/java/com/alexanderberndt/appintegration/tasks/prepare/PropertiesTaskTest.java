package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestAppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.testsupport.TestGlobalContext;
import com.alexanderberndt.appintegration.engine.testsupport.TestLoadingTask;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.simple.SimplePipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PropertiesTaskTest {

    private final TestAppIntegrationFactory appIntegrationFactory = new TestAppIntegrationFactory();

    @Mock
    private LogAppender logAppenderMock;

    private TestGlobalContext globalContext;

    private ProcessingPipeline pipeline;

    private TestValue<String> stringValue;

    private TestValue<Integer> numberValue;


    @BeforeEach
    void beforeEach() {
        globalContext = new TestGlobalContext(logAppenderMock);

        pipeline = new SimplePipelineBuilder()
                .addPreparationTask("properties", new PropertiesTask())
                .withTaskParam("verify:hello", "Hello World!")
                .withTaskParam("verify:hello.css", "This is a CSS value!")
                .withTaskParam("verify:the-number", 42)
                .addLoadingTask("load", new TestLoadingTask("Hello World!"))
                .addProcessingTask("verify", new ProcessingTask() {
                    @Override
                    public void process(@Nonnull TaskContext context, @Nonnull ExternalResource resource) {
                        stringValue.value = context.getValue("hello", String.class);
                        numberValue.value = context.getValue("the-number", Integer.class);
                    }
                })
                .build();

        pipeline.initContextWithTaskDefaults(globalContext);
        pipeline.initContextWithPipelineConfig(globalContext);

        stringValue = new TestValue<>();
        numberValue = new TestValue<>();
    }


    @Test
    void prepareTextRes() {
        final ExternalResourceRef resourceRef = ExternalResourceRef.create("/test.txt", ExternalResourceType.TEXT);
        pipeline.loadAndProcessResourceRef(globalContext, resourceRef, appIntegrationFactory.getExternalResourceFactory());
        assertEquals("Hello World!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }

    @Test
    void prepareCssRes() {
        final ExternalResourceRef resourceRef = ExternalResourceRef.create("/test.css", ExternalResourceType.CSS);
        pipeline.loadAndProcessResourceRef(globalContext, resourceRef, appIntegrationFactory.getExternalResourceFactory());
        assertEquals("This is a CSS value!", stringValue.value);
        assertEquals(new Integer(42), numberValue.value);
    }


    private static class TestValue<T> {
        T value;
    }
}