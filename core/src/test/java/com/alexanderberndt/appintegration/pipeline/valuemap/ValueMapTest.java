package com.alexanderberndt.appintegration.pipeline.valuemap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.alexanderberndt.appintegration.pipeline.context.Context.Ranking.*;
import static org.junit.jupiter.api.Assertions.*;

class ValueMapTest {

    private static final String NAMESPACE = "value-map-test";

    public static final String INT_VAR = "integer-variable";
    public static final String BOOL_VAR = "boolean-variable";
    public static final String STRING_VAR = "string-variable";
    public static final String UNDEFINED_VAR = "undefined-variable";

    private ValueMap valueMap;

    @BeforeEach
    void beforeEach() throws ValueException {
        valueMap = new ValueMap();
        valueMap.setValue(NAMESPACE, INT_VAR, PIPELINE_EXECUTION, 42);
        valueMap.setValue(NAMESPACE, BOOL_VAR, PIPELINE_EXECUTION, true);
        valueMap.setValue(NAMESPACE, STRING_VAR, PIPELINE_EXECUTION, "Hello world!");
    }

    @Test
    void getValue() {
        assertEquals(42, valueMap.getValue(NAMESPACE, INT_VAR));
        assertEquals(true, valueMap.getValue(NAMESPACE, BOOL_VAR));
        assertEquals("Hello world!", valueMap.getValue(NAMESPACE, STRING_VAR));
    }

    @Test
    void getValueWithDefault() throws ValueException {
        assertEquals(new Integer(42), valueMap.getValue(NAMESPACE, INT_VAR, -1));
        assertEquals(new Integer(-1), valueMap.getValue(NAMESPACE, UNDEFINED_VAR, -1));
    }

    @Test
    void getValueWithType() throws ValueException {
        assertEquals(new Integer(42), valueMap.getValue(NAMESPACE, INT_VAR, Integer.class));
        assertNull(valueMap.getValue(NAMESPACE, UNDEFINED_VAR, Integer.class));
    }

    @Test
    void getValueWithWrongType() {
        assertThrows(ValueException.class, () -> valueMap.getValue(NAMESPACE, INT_VAR, String.class));
    }

    @Test
    void setValue() throws ValueException {
        valueMap.setValue(NAMESPACE, INT_VAR, PIPELINE_EXECUTION, 100);
        assertEquals(100, valueMap.getValue(NAMESPACE, INT_VAR));
    }

    @Test
    void setValueWithHigherRank() throws ValueException {
        valueMap.setValue(NAMESPACE, INT_VAR, GLOBAL, 100);
        assertEquals(100, valueMap.getValue(NAMESPACE, INT_VAR));
    }

    @Test
    void setValueWithLowerRank() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE, INT_VAR, PIPELINE_DEFINITION, 100));
    }

    @Test
    void setValueWithWrongType() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE, INT_VAR, PIPELINE_EXECUTION, "something"));
    }

    @Test
    void setNullValueForNumber() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE, INT_VAR, PIPELINE_EXECUTION, null));
    }

    @Test
    void setNullValueForBoolean() {
        assertThrows(ValueException.class, () -> valueMap.setValue(NAMESPACE, BOOL_VAR, PIPELINE_EXECUTION, null));
    }

    @Test
    void setNullValueForString() throws ValueException {
        valueMap.setValue(NAMESPACE, STRING_VAR, PIPELINE_EXECUTION, null);
        assertNull(valueMap.getValue(NAMESPACE, STRING_VAR));
    }

    @Test
    void setNullValueForUndefined() throws ValueException {
        valueMap.setValue(NAMESPACE, UNDEFINED_VAR, PIPELINE_EXECUTION, null);
        assertNull(valueMap.getValue(NAMESPACE, UNDEFINED_VAR));
    }


}