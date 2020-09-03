package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.core.CoreTestGlobalContext;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskContextTest {

    public static final String MY_NAMESPACE = "my-namespace";

    @Mock
    private ResourceLoader resourceLoaderMock;

    private GlobalContext globalContext;

    private TaskContext taskContext;

    private TaskLogger taskLogger;

    private LogAppender logAppenderMock;


    @BeforeEach
    void beforeEach() {
        assertNotNull(resourceLoaderMock);
        logAppenderMock = Mockito.spy(new Slf4jLogAppender());
        GlobalContext tmpCtx = new CoreTestGlobalContext(logAppenderMock);
        tmpCtx.setResourceLoader(resourceLoaderMock);
        globalContext = Mockito.spy(tmpCtx);
        taskLogger = new TaskLogger(logAppenderMock, "test task", "test");
        this.taskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.TASK_DEFAULT, MY_NAMESPACE, ExternalResourceType.ANY, Collections.emptyMap()));
    }

    @Test
    void getUnqualifiedNamespaceKey() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-variable", true, "hello %a %d", "test", 42);
        assertEquals("my-namespace", nk.getNamespace());
        assertEquals("any-variable", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("my-namespace:any-variable", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKey1() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKey2() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.application-properties", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.APPLICATION_PROPERTIES, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKey2b() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.application-properties", false, "hello %s %d", "test", 42);
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other", nk.getPlainKey());
        Mockito.verify(globalContext).addWarning("my-namespace: hello test 42");
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKey3() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-other.application-properties", true, "xxx");
        assertEquals(MY_NAMESPACE, nk.getNamespace());
        assertEquals("any-other", nk.getKey());
        assertEquals(ExternalResourceType.APPLICATION_PROPERTIES, nk.getResourceType());
        assertEquals("my-namespace:any-other", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKeyInvalidType() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("any-other.xxx", true, "xxx");
        assertEquals(MY_NAMESPACE, nk.getNamespace());
        assertEquals("any-other.xxx", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("my-namespace:any-other.xxx", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getQualifiedNamespaceKeyInvalidType2() {
        TaskContext.NamespaceKey nk = taskContext.parseNamespaceKey("other:any-other.xxx", true, "xxx");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other.xxx", nk.getKey());
        assertEquals(ExternalResourceType.ANY, nk.getResourceType());
        assertEquals("other:any-other.xxx", nk.getPlainKey());
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
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
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongType() {
        taskContext.setValue("test1", 100);
        assertNull(taskContext.getValue("test1", String.class));
        Mockito.verify(globalContext).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithDefault() {
        taskContext.setValue("test1", 100);
        assertEquals(new Integer(100), taskContext.getValue("test1", 0));
        assertEquals(new Integer(0), taskContext.getValue("test2", 0));
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongDefaultType1() {
        taskContext.setValue("test1", 100);
        assertEquals("something", taskContext.getValue("test1", "something"));
        Mockito.verify(globalContext).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongDefaultType2() {
        taskContext.setType("test1", Integer.class);
        assertEquals("something", taskContext.getValue("test1", "something"));
        Mockito.verify(globalContext).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void setValue() {
        taskContext.setValue("test1", "Hello");
        assertEquals("Hello", taskContext.getValue("test1", "something"));
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());

//        assertEquals(Collections.singleton("test1"), pipelineConfiguration.keySet(MY_NAMESPACE));
//        assertEquals("Hello", pipelineConfiguration.getValue(MY_NAMESPACE, "test1", ExternalResourceType.ANY));
    }

    @Test
    void setValueWithWrongType() {
        taskContext.setType("test1", Integer.class);
        taskContext.setValue("test1", "Hello");
        Mockito.verify(globalContext).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void setType() {
        assertNull(taskContext.getType("test1"));
        taskContext.setType("test1", Integer.class);
        assertEquals(Integer.class, taskContext.getType("test1"));

        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());

        taskContext.setType("test1", String.class);
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext).addError(Mockito.anyString());
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
        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());

        taskContext.setValue("test3", "new key");
        assertNull(taskContext.getValue("test3"));
        Mockito.verify(globalContext).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void formatMessage() {
        assertEquals("hello", taskContext.formatMessage("hello"));
        assertEquals("hello world 42", taskContext.formatMessage("hello %s %d", "world", 42));
        assertEquals("hello", taskContext.formatMessage("hello", "world"));
        assertEquals("hello %a [world]", taskContext.formatMessage("hello %a", "world"));
    }

    @Test
    void getResourceLoader() {
        assertSame(resourceLoaderMock, taskContext.getResourceLoader());
    }

    @Test
    void getAndSetValueDuringExecution() {
        final Map<String, Object> executionDataMap = new HashMap<>();
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

        Mockito.verify(globalContext, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(globalContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getAndSetValueDuringExecutionWithTypeConflicts() {
        final Map<String, Object> executionDataMap = new HashMap<>();
        final TaskContext executionTaskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.PIPELINE_EXECUTION, MY_NAMESPACE, ExternalResourceType.HTML, executionDataMap));

        executionTaskContext.setValue("test", 10);
        taskContext.setValue("test", "Hello World!");

        assertEquals("Hello World!", executionTaskContext.getValue("test"));
        Mockito.verify(executionTaskContext).addWarning(Mockito.anyString());
        Mockito.verify(executionTaskContext, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void setValueDuringExecutionWithTypeConflicts() {
        final Map<String, Object> executionDataMap = new HashMap<>();
        final TaskContext executionTaskContext = Mockito.spy(new TaskContext(globalContext, taskLogger, Ranking.PIPELINE_EXECUTION, MY_NAMESPACE, ExternalResourceType.HTML, executionDataMap));

        taskContext.setValue("test", "Hello World!");
        executionTaskContext.setValue("test", 10);

        Mockito.verify(executionTaskContext).addWarning(Mockito.anyString());
        Mockito.verify(executionTaskContext, Mockito.never()).addError(Mockito.anyString());
    }
}