package com.alexanderberndt.appintegration.pipeline.valuemap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.alexanderberndt.appintegration.pipeline.context.Context.Ranking.*;
import static org.junit.jupiter.api.Assertions.*;

class ValueMapTest {

    private static final String NAMESPACE1 = "value-map-test";
    private static final String NAMESPACE2 = "more-namespace";

    public static final String INT_VAR = "integer-variable";
    public static final String BOOL_VAR = "boolean-variable";
    public static final String STRING_VAR = "string-variable";
    public static final String UNDEFINED_VAR = "undefined-variable";

    private ValueMap valueMap;

    @BeforeEach
    void beforeEach() throws ValueException {
        valueMap = new ValueMap();
        valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, 42);
        valueMap.setValue(NAMESPACE1, BOOL_VAR, PIPELINE_EXECUTION, true);
        valueMap.setValue(NAMESPACE1, STRING_VAR, PIPELINE_EXECUTION, "Hello world!");

        valueMap.setValue(NAMESPACE2, STRING_VAR, PIPELINE_EXECUTION, "This is more stuff!");
        valueMap.setValue(null, INT_VAR, PIPELINE_EXECUTION, 100);
    }

    @Test
    void getValue() {
        assertEquals(42, valueMap.getValue(NAMESPACE1, INT_VAR));
        assertEquals(true, valueMap.getValue(NAMESPACE1, BOOL_VAR));
        assertEquals("Hello world!", valueMap.getValue(NAMESPACE1, STRING_VAR));
        assertEquals("This is more stuff!", valueMap.getValue(NAMESPACE2, STRING_VAR));
        assertEquals(100, valueMap.getValue(null, INT_VAR));
    }

    @Test
    void getValueWithDefault() throws ValueException {
        assertEquals(new Integer(42), valueMap.getValue(NAMESPACE1, INT_VAR, -1));
        assertEquals(new Integer(-1), valueMap.getValue(NAMESPACE1, UNDEFINED_VAR, -1));
    }

    @Test
    void getValueWithType() throws ValueException {
        assertEquals(new Integer(42), valueMap.getValue(NAMESPACE1, INT_VAR, Integer.class));
        assertNull(valueMap.getValue(NAMESPACE1, UNDEFINED_VAR, Integer.class));
    }

    @Test
    void getValueWithWrongType() {
        assertThrows(ValueException.class, () -> valueMap.getValue(NAMESPACE1, INT_VAR, String.class));
    }

    @Test
    void requireValue() throws ValueException {
        assertEquals(new Integer(42), valueMap.requireValue(NAMESPACE1, INT_VAR, Integer.class));
        assertThrows(ValueException.class, () -> valueMap.requireValue(NAMESPACE1, UNDEFINED_VAR, Integer.class));
    }

    @Test
    void getType() {
        assertEquals(Integer.class, valueMap.getType(NAMESPACE1, INT_VAR));
        assertEquals(Boolean.class, valueMap.getType(NAMESPACE1, BOOL_VAR));
        assertEquals(String.class, valueMap.getType(NAMESPACE1, STRING_VAR));
        assertNull(valueMap.getType(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void getTypeName() {
        assertEquals("Integer", valueMap.getTypeName(NAMESPACE1, INT_VAR));
        assertEquals("Boolean", valueMap.getTypeName(NAMESPACE1, BOOL_VAR));
        assertEquals("String", valueMap.getTypeName(NAMESPACE1, STRING_VAR));
        assertNull(valueMap.getTypeName(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void namespaceSet() {
        Set<String> namespaceSet = valueMap.namespaceSet();
        assertNotNull(namespaceSet);
        assertEquals(3, namespaceSet.size());
        assertTrue(namespaceSet.containsAll(Arrays.asList(NAMESPACE1, NAMESPACE2, "global")));
    }

    @Test
    void keySet1() {
        Set<String> keySet = valueMap.keySet(NAMESPACE1);
        assertNotNull(keySet);
        assertEquals(3, keySet.size());
        assertTrue(keySet.containsAll(Arrays.asList(INT_VAR, BOOL_VAR, STRING_VAR)));
    }

    @Test
    void keySet2() {
        Set<String> keySet = valueMap.keySet(NAMESPACE2);
        assertNotNull(keySet);
        assertEquals(1, keySet.size());
        assertTrue(keySet.contains(STRING_VAR));
    }

    @Test
    void keySet3() {
        Set<String> keySet = valueMap.keySet(null);
        assertNotNull(keySet);
        assertEquals(1, keySet.size());
        assertTrue(keySet.contains(INT_VAR));
    }

    @Test
    void keySet4() {
        Set<String> keySet = valueMap.keySet("unknown-to-anybody");
        assertNotNull(keySet);
        assertTrue(keySet.isEmpty());
    }

    @Test
    void setValue() throws ValueException {
        valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, 100);
        assertEquals(100, valueMap.getValue(NAMESPACE1, INT_VAR));
    }

    @Test
    void setValueWithHigherRank() throws ValueException {
        valueMap.setValue(NAMESPACE1, INT_VAR, GLOBAL, 100);
        assertEquals(100, valueMap.getValue(NAMESPACE1, INT_VAR));
    }

    @Test
    void setValueWithLowerRank() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_DEFINITION, 100));
    }

    @Test
    void setValueWithWrongType() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, "something"));
    }

    @Test
    void setNullValueForNumber() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, null));
    }

    @Test
    void setNullValueForBoolean() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, BOOL_VAR, PIPELINE_EXECUTION, null));
    }

    @Test
    void setNullValueForString() throws ValueException {
        valueMap.setValue(NAMESPACE1, STRING_VAR, PIPELINE_EXECUTION, null);
        assertNull(valueMap.getValue(NAMESPACE1, STRING_VAR));
    }

    @Test
    void setNullValueForUndefined() throws ValueException {
        valueMap.setValue(NAMESPACE1, UNDEFINED_VAR, PIPELINE_EXECUTION, null);
        assertNull(valueMap.getValue(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void setType() throws ValueException {
        assertNull(valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, List.class);
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, List.class);
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        assertThrows(ValueException.class, () -> valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, Integer.class));
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_EXECUTION, List.class);
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, List.class);
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void clear() throws ValueException {
        assertEquals(42, valueMap.getValue(NAMESPACE1, INT_VAR));
        assertEquals(Integer.class, valueMap.getType(NAMESPACE1, INT_VAR));

        valueMap.setValue(NAMESPACE1, INT_VAR, GLOBAL, 100);
        assertEquals(100, valueMap.getValue(NAMESPACE1, INT_VAR));
        assertEquals(Integer.class, valueMap.getType(NAMESPACE1, INT_VAR));

        valueMap.clear(GLOBAL);
        assertEquals(42, valueMap.getValue(NAMESPACE1, INT_VAR));
        assertEquals(Integer.class, valueMap.getType(NAMESPACE1, INT_VAR));

        valueMap.clear(PIPELINE_EXECUTION);
        assertNull(valueMap.getValue(NAMESPACE1, INT_VAR));
        assertNull(valueMap.getType(NAMESPACE1, INT_VAR));
    }


    @Test
    void setKeyComplete() {
        valueMap.setKeyComplete(NAMESPACE1);
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, "something-new", PIPELINE_EXECUTION, 300));
        assertDoesNotThrow(() -> valueMap.setValue(NAMESPACE2, "something-new", PIPELINE_EXECUTION, 300));
    }

    @Test
    void setReadOnly() throws ValueException {
        valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, 200);
        assertEquals(200, valueMap.getValue(NAMESPACE1, INT_VAR));

        valueMap.setReadOnly(PIPELINE_EXECUTION);
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, 300));
        assertEquals(200, valueMap.getValue(NAMESPACE1, INT_VAR));

        assertThrows(ValueException.class, () -> valueMap.setType(NAMESPACE1, UNDEFINED_VAR, PIPELINE_EXECUTION, List.class));
        assertNull(valueMap.getType(NAMESPACE1, UNDEFINED_VAR));

        valueMap.setValue(NAMESPACE1, INT_VAR, GLOBAL, 400);
        assertEquals(400, valueMap.getValue(NAMESPACE1, INT_VAR));

        valueMap.setType(NAMESPACE1, UNDEFINED_VAR, GLOBAL, List.class);
        assertEquals(List.class, valueMap.getType(NAMESPACE1, UNDEFINED_VAR));
    }

}