package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.core.CoreTestGlobalContext;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalContextTest {

    @Mock
    private ResourceLoader resourceLoaderMock;

    private GlobalContext globalContext;

    @BeforeEach
    void setUp() {
        assertNotNull(resourceLoaderMock);
        globalContext = new CoreTestGlobalContext(resourceLoaderMock, new Slf4jLogAppender());
    }

    @Test
    void createTaskContext() {
        assertNotNull(globalContext.createTaskContext(Mockito.mock(TaskLogger.class), Ranking.PIPELINE_DEFINITION, "my-task", ExternalResourceType.ANY, Collections.emptyMap()));
    }

    @Test
    void getResourceLoader() {
        assertSame(resourceLoaderMock, globalContext.getResourceLoader());
    }

    @Test
    void getProcessingParams() {
        assertNotNull(globalContext.getProcessingParams());
    }

    @Test
    void addWarning() {
        // ToDo: Extend, as soon as warnings and errors are collected
        assertDoesNotThrow(() -> globalContext.addWarning("warn"));
    }

    @Test
    void addError() {
        // ToDo: Extend, as soon as warnings and errors are collected
        assertDoesNotThrow(() -> globalContext.addError("error"));
    }
}