package com.alexanderberndt.appintegration.pipeline.valuemap;

import com.alexanderberndt.appintegration.pipeline.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScopedValueMapFacadeTest {

    public static final String MY_NAMESPACE = "my-namespace";

    private ValueMap valueMap;

    private ScopedValueMapFacade scopedValueMap;

    @Mock
    private Context contextMock;


    @BeforeEach
    void beforeEach() {
        this.valueMap = new ValueMap();
        this.scopedValueMap = new ScopedValueMapFacade(this.contextMock, this.valueMap);

        Mockito.lenient().when(contextMock.getNamespace()).thenReturn(MY_NAMESPACE);
        Mockito.lenient().when(contextMock.getRank()).thenReturn(Context.Ranking.PIPELINE_DEFINITION);
    }

    @Test
    void getUnqualifiedNamespaceKey() {
        ScopedValueMapFacade.NamespaceKey nk = scopedValueMap.getNamespaceKey("any-variable");
        assertEquals("my-namespace", nk.getNamespace());
        assertEquals("any-variable", nk.getKey());
    }

    @Test
    void getQualifiedNamespaceKey() {
        ScopedValueMapFacade.NamespaceKey nk = scopedValueMap.getNamespaceKey("other:any-other");
        assertEquals("other", nk.getNamespace());
        assertEquals("any-other", nk.getKey());
    }

    @Test
    void getValue() {
        scopedValueMap.setValue("test1", 100);
        assertEquals(100, scopedValueMap.getValue("test1"));
    }

    @Test
    void getValueWithType() {
        scopedValueMap.setValue("test1", 100);
        assertEquals(new Integer(100), scopedValueMap.getValue("test1", Integer.class));
        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongType() {
        scopedValueMap.setValue("test1", 100);
        assertNull(scopedValueMap.getValue("test1", String.class));
        Mockito.verify(contextMock).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithDefault() {
        scopedValueMap.setValue("test1", 100);
        assertEquals(new Integer(100), scopedValueMap.getValue("test1", 0));
        assertEquals(new Integer(0), scopedValueMap.getValue("test2", 0));
        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongDefaultType1() {
        scopedValueMap.setValue("test1", 100);
        assertEquals("something", scopedValueMap.getValue("test1", "something"));
        Mockito.verify(contextMock).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void getValueWithWrongDefaultType2() {
        scopedValueMap.setType("test1", Integer.class);
        assertEquals("something", scopedValueMap.getValue("test1", "something"));
        Mockito.verify(contextMock).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void setValue() {
        scopedValueMap.setValue("test1", "Hello");
        assertEquals("Hello", scopedValueMap.getValue("test1", "something"));
        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());

        assertEquals(Collections.singleton("test1"), valueMap.keySet(MY_NAMESPACE));
        assertEquals("Hello", valueMap.getValue(MY_NAMESPACE, "test1"));
    }

    @Test
    void setValueWithWrongType() {
        scopedValueMap.setType("test1", Integer.class);
        scopedValueMap.setValue("test1", "Hello");
        Mockito.verify(contextMock).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }

    @Test
    void setType() {
        assertNull(scopedValueMap.getType("test1"));
        scopedValueMap.setType("test1", Integer.class);
        assertEquals(Integer.class, scopedValueMap.getType("test1"));

        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());

        scopedValueMap.setType("test1", String.class);
        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock).addError(Mockito.anyString());
    }

    @Test
    void entrySet() {
        assertEquals(0, scopedValueMap.keySet().size());
        scopedValueMap.setValue("test1", "Hello");
        scopedValueMap.setValue("test2", "World!");
        assertEquals(2, scopedValueMap.keySet().size());
        assertTrue(scopedValueMap.keySet().containsAll(Arrays.asList("test1", "test2")));
    }

    @Test
    void setKeyComplete() {
        scopedValueMap.setValue("test1", "Hello");
        scopedValueMap.setValue("test2", "World!");

        scopedValueMap.setKeyComplete();

        assertEquals("World!", scopedValueMap.getValue("test2"));
        scopedValueMap.setValue("test2", "Changed value");
        assertEquals("Changed value", scopedValueMap.getValue("test2"));


        Mockito.verify(contextMock, Mockito.never()).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());

        scopedValueMap.setValue("test3", "new key");
        assertNull(scopedValueMap.getValue("test3"));
        Mockito.verify(contextMock).addWarning(Mockito.anyString());
        Mockito.verify(contextMock, Mockito.never()).addError(Mockito.anyString());
    }
}