package com.alexanderberndt.appintegration.pipeline.configuration;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiValueTest {

    @Test
    void constructor() {
        MultiValue value = new MultiValue();
        assertNull(value.getValue(ExternalResourceType.HTML));
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    void createByValue1() {
        MultiValue value = MultiValue.createByValue(Ranking.PIPELINE_EXECUTION, ExternalResourceType.ANY, 100);
        assertEquals(100, value.getValue(ExternalResourceType.ANY));
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void createByValue2() {
        MultiValue value = MultiValue.createByValue(Ranking.PIPELINE_EXECUTION, ExternalResourceType.ANY, null);
        assertNull(value.getValue(ExternalResourceType.ANY));
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    void setValue1() throws ConfigurationException {
        MultiValue value = new MultiValue();
        value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 100);

        assertEquals(100, value.getValue(ExternalResourceType.ANY));
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void setValue2() throws ConfigurationException {
        MultiValue value = new MultiValue();
        value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, null);

        assertNull(value.getValue(ExternalResourceType.ANY));
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

      @Test
    void setStringTypeToNull() {
        MultiValue value1 = MultiValue.createByType(String.class);
        assertDoesNotThrow(() -> value1.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, null));

        MultiValue value = MultiValue.createByValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, "Hello World");
        assertDoesNotThrow(() -> value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, null));
    }

    @Test
    void setWithDifferentType() throws ConfigurationException {
        MultiValue value1 = MultiValue.createByType(Integer.class);
        assertThrows(ConfigurationException.class, () -> value1.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, "Hello"));

        MultiValue value2 = new MultiValue();
        value2.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 100);
        assertThrows(ConfigurationException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, "Hello"));
        assertThrows(ConfigurationException.class, () -> value2.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, false));
        assertEquals(100, value2.getValue(ExternalResourceType.ANY));
    }

    @Test
    void setValuesWithDifferentRanks() throws ConfigurationException {
        MultiValue value = MultiValue.createByValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 100);
        assertEquals(100, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.TASK_DEFAULT, ExternalResourceType.ANY, 101);
        assertEquals(100, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 200);
        assertEquals(200, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.PIPELINE_EXECUTION, ExternalResourceType.ANY, 300);
        assertEquals(300, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 400);
        assertEquals(300, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.PIPELINE_EXECUTION, ExternalResourceType.ANY, 500);
        assertEquals(500, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.GLOBAL, ExternalResourceType.ANY, 600);
        assertEquals(600, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.TASK_DEFAULT, ExternalResourceType.ANY, 700);
        value.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.ANY, 700);
        value.setValue(Ranking.PIPELINE_EXECUTION, ExternalResourceType.ANY, 700);
        assertEquals(600, value.getValue(ExternalResourceType.ANY));

        value.setValue(Ranking.GLOBAL, ExternalResourceType.ANY, 700);
        assertEquals(700, value.getValue(ExternalResourceType.ANY));
    }


    @Test
    void createByType1() {
        MultiValue value = MultiValue.createByType(Integer.class);
        assertNull(value.getValue(ExternalResourceType.ANY));
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void createByType2() {
        MultiValue value = MultiValue.createByType(null);
        assertNull(value.getValue(ExternalResourceType.ANY));
        assertNull(value.getType());
        assertNull(value.getTypeName());
    }

    @Test
    void setType() throws ConfigurationException {
        MultiValue value = new MultiValue();
        value.setType(Integer.class);

        assertNull(value.getValue(ExternalResourceType.ANY));
        assertEquals(Integer.class, value.getType());
        assertEquals("Integer", value.getTypeName());
    }

    @Test
    void setTypesWithDifferentRanks() throws ConfigurationException {
        MultiValue value = new MultiValue();

        value.setType(String.class);
        assertEquals(String.class, value.getType());

        assertThrows(ConfigurationException.class, () -> value.setType(Integer.class));
        assertEquals(String.class, value.getType());

        value.setType(String.class);
        assertEquals(String.class, value.getType());

        value.setType(String.class);
        assertEquals(String.class, value.getType());

        value.setType(String.class);
        assertEquals(String.class, value.getType());

        assertThrows(ConfigurationException.class, () -> value.setType(Integer.class));
        assertEquals(String.class, value.getType());
    }

    @Test
    void setAndGetValueWithDifferentResourceTypes() throws ConfigurationException {
        MultiValue multiValue = new MultiValue();

        multiValue.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.TEXT, "Hello World");
        multiValue.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.CSS, "Hello with Style");

        assertEquals("Hello World", multiValue.getValue(ExternalResourceType.TEXT));
        assertEquals("Hello World", multiValue.getValue(ExternalResourceType.HTML));
        assertEquals("Hello with Style", multiValue.getValue(ExternalResourceType.CSS));
        assertNull(multiValue.getValue(ExternalResourceType.BINARY));
    }

    @Test
    void setAndGetValueWithDifferentResourceTypesAndExecutionValues() throws ConfigurationException {
        MultiValue multiValue = new MultiValue();

        multiValue.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.TEXT, "Hello World");
        multiValue.setValue(Ranking.PIPELINE_DEFINITION, ExternalResourceType.CSS, "Hello with Style");
        multiValue.setValue(Ranking.GLOBAL, ExternalResourceType.CSS, "Hello with Extra-Style");

        assertEquals("Bye", multiValue.getValue(ExternalResourceType.TEXT, "Bye"));
        assertEquals("Hello World", multiValue.getValue(ExternalResourceType.HTML));
        assertEquals("Bye", multiValue.getValue(ExternalResourceType.HTML, "Bye"));
        assertEquals("Hello with Extra-Style", multiValue.getValue(ExternalResourceType.CSS, "Bye"));
        assertNull(multiValue.getValue(ExternalResourceType.BINARY));
    }
}