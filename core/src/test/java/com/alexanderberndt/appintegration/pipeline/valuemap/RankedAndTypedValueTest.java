package com.alexanderberndt.appintegration.pipeline.valuemap;

import com.alexanderberndt.appintegration.pipeline.context.Context;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RankedAndTypedValueTest {


    @Test
    void testSetValueByRank() throws ValueException {
        RankedAndTypedValue value = new RankedAndTypedValue(Context.Ranking.PIPELINE_DEFINITION, 100);
        assertEquals(100, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Context.Ranking.TASK_DEFAULT, 101));
        assertEquals(100, value.getValue());

        value.setValue(Context.Ranking.PIPELINE_DEFINITION, 200);
        assertEquals(200, value.getValue());

        value.setValue(Context.Ranking.PIPELINE_EXECUTION, 300);
        assertEquals(300, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Context.Ranking.PIPELINE_DEFINITION, 400));
        assertEquals(300, value.getValue());

        value.setValue(Context.Ranking.PIPELINE_EXECUTION, 500);
        assertEquals(500, value.getValue());

        value.setValue(Context.Ranking.GLOBAL, 600);
        assertEquals(600, value.getValue());

        assertThrows(ValueException.class, () -> value.setValue(Context.Ranking.TASK_DEFAULT, 700));
        assertThrows(ValueException.class, () -> value.setValue(Context.Ranking.PIPELINE_DEFINITION, 700));
        assertThrows(ValueException.class, () -> value.setValue(Context.Ranking.PIPELINE_EXECUTION, 700));
        assertEquals(600, value.getValue());

        value.setValue(Context.Ranking.GLOBAL, 700);
        assertEquals(700, value.getValue());
    }

    @Test
    void setValueWithoutRank() {
        RankedAndTypedValue value = new RankedAndTypedValue(null, null);
        assertThrows(ValueException.class, () -> value.setValue(null, 700));
    }

    @Test
    void setTypeWithoutRank() {
        RankedAndTypedValue value = new RankedAndTypedValue(null, null);
        assertThrows(ValueException.class, () -> value.setType(null, null));
        assertThrows(ValueException.class, () -> value.setType(null, Integer.class));
    }


    @Test
    @Disabled
    void getType() {
    }

    @Test
    @Disabled
    void getTypeName() {
    }

    @Test
    @Disabled
    void setType() {
    }

    @Test
    @Disabled
    void getValue() {
    }

    @Test
    @Disabled
    void setValue() {
    }

    @Test
    @Disabled
    void isNullableType() {
    }

    @Test
    void isNumberType() {
    }

    @Test
    void simpleName() {
    }
}