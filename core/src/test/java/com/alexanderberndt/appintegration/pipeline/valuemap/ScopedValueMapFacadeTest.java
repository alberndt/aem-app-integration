package com.alexanderberndt.appintegration.pipeline.valuemap;

import com.alexanderberndt.appintegration.pipeline.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testGetValue() {
    }

    @Test
    void testGetValue1() {
    }

    @Test
    void setValue() {
    }

    @Test
    void entrySet() {
    }

    @Test
    void setKeyComplete() {
    }
}