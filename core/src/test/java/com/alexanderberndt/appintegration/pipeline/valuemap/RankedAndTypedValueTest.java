package com.alexanderberndt.appintegration.pipeline.valuemap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankedAndTypedValueTest {

    @Test
    void constructor() {
        RankedAndTypedValue value = new RankedAndTypedValue();
        assertNull(value.getValue());
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    void createByValue1() {
        RankedAndTypedValue value = RankedAndTypedValue.createByValue(Ranking.PIPELINE_EXECUTION, 100);
        assertEquals(100, value.getValue());
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void createByValue2() {
        RankedAndTypedValue value = RankedAndTypedValue.createByValue(Ranking.PIPELINE_EXECUTION, null);
        assertNull(value.getValue());
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void createByValue3() {
        assertThrows(IllegalArgumentException.class, () -> RankedAndTypedValue.createByValue(null, 100));
        assertThrows(IllegalArgumentException.class, () -> RankedAndTypedValue.createByValue(null, null));
    }

    @Test
    void setValue1() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue();
        value.setValue(Ranking.PIPELINE_DEFINITION, 100);

        assertEquals(100, value.getValue());
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void setValue2() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue();
        value.setValue(Ranking.PIPELINE_DEFINITION, null);

        assertNull(value.getValue());
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setValue3() {
        RankedAndTypedValue value = new RankedAndTypedValue();

        assertThrows(IllegalArgumentException.class, () -> value.setValue(null, 100));
        assertThrows(IllegalArgumentException.class, () -> value.setValue(null, null));
    }

    @Test
    void setNumberTypeToNull() {
        RankedAndTypedValue value1 = RankedAndTypedValue.createByType(Ranking.PIPELINE_DEFINITION, Integer.class);
        assertThrows(ValueException.class, () -> value1.setValue(Ranking.PIPELINE_DEFINITION, null));

        RankedAndTypedValue value2 = RankedAndTypedValue.createByValue(Ranking.PIPELINE_DEFINITION, 100);
        assertThrows(ValueException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, null));
    }

    @Test
    void setBooleanTypeToNull() {
        RankedAndTypedValue value1 = RankedAndTypedValue.createByType(Ranking.PIPELINE_DEFINITION, Boolean.class);
        assertThrows(ValueException.class, () -> value1.setValue(Ranking.PIPELINE_DEFINITION, null));

        RankedAndTypedValue value2 = RankedAndTypedValue.createByValue(Ranking.PIPELINE_DEFINITION, true);
        assertThrows(ValueException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, null));
    }

    @Test
    void setStringTypeToNull() {
        RankedAndTypedValue value1 = RankedAndTypedValue.createByType(Ranking.PIPELINE_DEFINITION, String.class);
        assertDoesNotThrow(() -> value1.setValue(Ranking.PIPELINE_DEFINITION, null));

        RankedAndTypedValue value = RankedAndTypedValue.createByValue(Ranking.PIPELINE_DEFINITION, "Hello World");
        assertDoesNotThrow(() -> value.setValue(Ranking.PIPELINE_DEFINITION, null));
    }

    @Test
    void setWithDifferentType() throws ValueException {
        RankedAndTypedValue value1 = RankedAndTypedValue.createByType(Ranking.PIPELINE_DEFINITION, Integer.class);
        assertThrows(ValueException.class, () -> value1.setValue(Ranking.PIPELINE_DEFINITION, "Hello"));

        RankedAndTypedValue value2 = new RankedAndTypedValue();
        value2.setValue(Ranking.PIPELINE_DEFINITION, 100);
        assertThrows(ValueException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, "Hello"));
        assertThrows(ValueException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, false));
        assertEquals(100, value2.getValue());
    }

    @Test
    void setValuesWithDifferentRanks() throws ValueException {
        RankedAndTypedValue value = RankedAndTypedValue.createByValue(Ranking.PIPELINE_DEFINITION, 100);
        assertEquals(100, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Ranking.TASK_DEFAULT, 101));
        assertEquals(100, value.getValue());

        value.setValue(Ranking.PIPELINE_DEFINITION, 200);
        assertEquals(200, value.getValue());

        value.setValue(Ranking.PIPELINE_EXECUTION, 300);
        assertEquals(300, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Ranking.PIPELINE_DEFINITION, 400));
        assertEquals(300, value.getValue());

        value.setValue(Ranking.PIPELINE_EXECUTION, 500);
        assertEquals(500, value.getValue());

        value.setValue(Ranking.GLOBAL, 600);
        assertEquals(600, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Ranking.TASK_DEFAULT, 700));
        assertThrows(ValueException.class, () -> value.setValue(Ranking.PIPELINE_DEFINITION, 700));
        assertThrows(ValueException.class, () -> value.setValue(Ranking.PIPELINE_EXECUTION, 700));
        assertEquals(600, value.getValue());

        value.setValue(Ranking.GLOBAL, 700);
        assertEquals(700, value.getValue());
    }


    @Test
    void createByType1() {
        RankedAndTypedValue value = RankedAndTypedValue.createByType(Ranking.PIPELINE_EXECUTION, Integer.class);
        assertNull(value.getValue());
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void createByType2() {
        RankedAndTypedValue value = RankedAndTypedValue.createByType(Ranking.PIPELINE_EXECUTION, null);
        assertNull(value.getValue());
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void createByType3() {
        assertThrows(IllegalArgumentException.class, () -> RankedAndTypedValue.createByValue(null, 100));
        assertThrows(IllegalArgumentException.class, () -> RankedAndTypedValue.createByValue(null, null));
    }

    @Test
    void setType1() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue();
        value.setType(Ranking.PIPELINE_EXECUTION, Integer.class);

        assertNull(value.getValue());
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setType2() {
        RankedAndTypedValue value = new RankedAndTypedValue();
        assertThrows(NullPointerException.class, () -> value.setType(Ranking.PIPELINE_EXECUTION, null));

        assertNull(value.getValue());
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setType3() {
        RankedAndTypedValue value = new RankedAndTypedValue();
        assertThrows(IllegalArgumentException.class, () -> value.setType(null, String.class));
        assertThrows(IllegalArgumentException.class, () -> value.setType(null, null));
    }

    @Test
    void setTypesWithDifferentRanks() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue();

        value.setType(Ranking.PIPELINE_DEFINITION, String.class);
        assertEquals(String.class, value.getType());

        assertThrows(ValueException.class, () -> value.setType(Ranking.PIPELINE_DEFINITION, Integer.class));
        assertEquals(String.class, value.getType());

        value.setType(Ranking.PIPELINE_EXECUTION, String.class);
        assertEquals(String.class, value.getType());

        value.setType(Ranking.TASK_DEFAULT, String.class);
        assertEquals(String.class, value.getType());

        value.setType(Ranking.PIPELINE_DEFINITION, String.class);
        assertEquals(String.class, value.getType());

        assertThrows(ValueException.class, () -> value.setType(Ranking.GLOBAL, Integer.class));
        assertEquals(String.class, value.getType());
    }

    @Test
    void clear1() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue();
        value.setValue(Ranking.PIPELINE_EXECUTION, 100);
        assertEquals(100, value.getValue());
        assertEquals(Integer.class, value.getType());

        value.clear(Ranking.PIPELINE_EXECUTION);
        assertNull(value.getValue());
        assertNull(value.getType());
    }


    @Test
    void clear2() throws ValueException {

        RankedAndTypedValue value = new RankedAndTypedValue();

        value.setType(Ranking.TASK_DEFAULT, String.class);
        value.setValue(Ranking.PIPELINE_DEFINITION, "Hello!");
        value.setValue(Ranking.PIPELINE_EXECUTION, "Ignore me!");
        value.setValue(Ranking.GLOBAL, "World!");
        assertEquals("World!", value.getValue());
        assertEquals(String.class, value.getType());

        value.clear(Ranking.PIPELINE_EXECUTION);
        assertEquals("World!", value.getValue());
        assertEquals(String.class, value.getType());

        value.clear(Ranking.GLOBAL);
        assertEquals("Hello!", value.getValue());
        assertEquals(String.class, value.getType());

        value.clear(Ranking.PIPELINE_DEFINITION);
        assertNull(value.getValue());
        assertEquals(String.class, value.getType());

        value.clear(Ranking.TASK_DEFAULT);
        assertNull(value.getValue());
        assertNull(value.getType());


        value.setValue(Ranking.PIPELINE_EXECUTION, 100);
        assertEquals(100, value.getValue());
        assertEquals(Integer.class, value.getType());
    }
}