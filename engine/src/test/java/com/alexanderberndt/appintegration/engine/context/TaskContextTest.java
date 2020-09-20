package com.alexanderberndt.appintegration.engine.context;

import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.testsupport.TestGlobalContext;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskContextTest {

    public static final String MY_NAMESPACE = "my-namespace";

    @Mock
    private TestGlobalContext globalContext;

    @Mock
    private LogAppender logAppenderMock;

    private TaskLogger taskLogger;

    private TaskContext taskContext;

    private PipelineConfiguration pipelineConfiguration;


    @BeforeEach
    void beforeEach() {
        this.pipelineConfiguration = new PipelineConfiguration();
        assertNotNull(globalContext);
        Mockito.lenient().when(globalContext.getProcessingParams()).thenReturn(pipelineConfiguration);

        taskLogger = new TaskLogger(logAppenderMock, "test", "test task");
        this.taskContext = new TaskContext(globalContext, taskLogger, Ranking.TASK_DEFAULT, MY_NAMESPACE, ExternalResourceType.ANY, new DataMap());
    }

    @Test
    void getUnqualifiedNamespaceKey() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-variable", true, "hello %a %d", "test", 42);
        assertEquals("my-namespace", nk.getNamespace());
        assertEquals("any-variable", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("my-namespace:any-variable", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKey1() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKey2() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.application-properties", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.APPLICATION_PROPERTIES, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKey2b() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.application-properties", false, "hello %s %d", "test", 42);
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), eq("my-namespace: hello test 42"));
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKey3() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-other.application-properties", true, "xxx");
        assertEquals(MY_NAMESPACE, nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.APPLICATION_PROPERTIES, nk.getResourceType());
        assertEquals("my-namespace:any-other", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKeyInvalidType() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-other.xxx", true, "xxx");
        assertEquals(MY_NAMESPACE, nk.getNamespace());
        assertEquals("any-other.xxx", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("my-namespace:any-other.xxx", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getQualifiedNamespaceKeyInvalidType2() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.xxx", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other.xxx", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other.xxx", nk.getPlainKey());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getValue() {
        taskContext.setValue("test1", 100);
        assertEquals(100, taskContext.getValue("test1"));
    }

    @Test
    void getValueWithType() {
        taskContext.setValue("test1", 100);
        assertEquals(new Integer(100), taskContext.getValue("test1", Integer.class));
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getValueWithWrongType() {
        taskContext.setValue("test1", 100);
        assertNull(taskContext.getValue("test1", String.class));
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getValueWithDefault() {
        taskContext.setValue("test1", 100);
        assertEquals(new Integer(100), taskContext.getValue("test1", 0));
        assertEquals(new Integer(0), taskContext.getValue("test2", 0));
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getValueWithWrongDefaultType1() {
        taskContext.setValue("test1", 100);
        assertEquals("something", taskContext.getValue("test1", "something"));
        verify(logAppenderMock, atLeastOnce()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());

    }

    @Test
    void getValueWithWrongDefaultType2() {
        taskContext.setType("test1", Integer.class);
        assertEquals("something", taskContext.getValue("test1", "something"));
        verify(logAppenderMock, atLeastOnce()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void setValue() {
        taskContext.setValue("test1", "Hello");
        assertEquals("Hello", taskContext.getValue("test1", "something"));
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());

        assertEquals(Collections.singleton("test1"), pipelineConfiguration.keySet(MY_NAMESPACE));
        assertEquals("Hello", pipelineConfiguration.getValue(MY_NAMESPACE, "test1", ExternalResourceType.ANY));
    }

    @Test
    void setValueWithWrongType() {
        taskContext.setType("test1", Integer.class);
        taskContext.setValue("test1", "Hello");
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void setType() {
        assertNull(taskContext.getType("test1"));
        taskContext.setType("test1", Integer.class);
        assertEquals(Integer.class, taskContext.getType("test1"));

        verify(logAppenderMock, never()).appendLogEntry(any(), any(), anyString());

        taskContext.setType("test1", String.class);
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void entrySet() {
        assertEquals(0, taskContext.keySet().size());
        taskContext.setValue("test1", "Hello");
        taskContext.setValue("test2", "World!");
        assertEquals(2, taskContext.keySet().size());
        assertTrue(taskContext.keySet().containsAll(Arrays.asList("test1", "test2")));
    }

    @Test
    void setKeyComplete() {
        taskContext.setValue("test1", "Hello");
        taskContext.setValue("test2", "World!");

        taskContext.setKeyComplete();

        assertEquals("World!", taskContext.getValue("test2"));
        taskContext.setValue("test2", "Changed value");
        assertEquals("Changed value", taskContext.getValue("test2"));
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());

        taskContext.setValue("test3", "new key");
        assertNull(taskContext.getValue("test3"));
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getAndSetValueDuringExecution() {
        final DataMap executionDataMap = new DataMap();
        final TaskContext executionTaskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.PIPELINE_EXECUTION, MY_NAMESPACE, ExternalResourceType.HTML, executionDataMap));

        taskContext.setValue("test", "Hello World!");
        assertTrue(executionDataMap.isEmpty());
        assertEquals("Hello World!", executionTaskContext.getValue("test"));
        assertEquals("Hello World!", taskContext.getValue("test"));

        executionTaskContext.setValue("test", "Now running...");
        assertEquals(1, executionDataMap.size());
        assertEquals("Now running...", executionDataMap.get("my-namespace:test"));
        assertEquals("Now running...", executionTaskContext.getValue("test"));
        assertEquals("Hello World!", taskContext.getValue("test"));

        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void getAndSetValueDuringExecutionWithTypeConflicts() {
        final DataMap executionDataMap = new DataMap();
        final TaskContext executionTaskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.PIPELINE_EXECUTION, MY_NAMESPACE, ExternalResourceType.HTML, executionDataMap));

        executionTaskContext.setValue("test", 10);
        taskContext.setValue("test", "Hello World!");

        assertEquals("Hello World!", executionTaskContext.getValue("test"));
        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }

    @Test
    void setValueDuringExecutionWithTypeConflicts() {
        final DataMap executionDataMap = new DataMap();
        final TaskContext executionTaskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.PIPELINE_EXECUTION, MY_NAMESPACE, ExternalResourceType.HTML, executionDataMap));

        taskContext.setValue("test", "Hello World!");
        executionTaskContext.setValue("test", 10);

        verify(logAppenderMock).appendLogEntry(any(), eq(LogStatus.WARNING), anyString());
        verify(logAppenderMock, never()).appendLogEntry(any(), eq(LogStatus.ERROR), anyString());
    }
}