package com.alexanderberndt.appintegration.pipeline.configuration;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.alexanderberndt.appintegration.pipeline.configuration.Ranking.*;
import static org.junit.jupiter.api.Assertions.*;

class PipelineConfigurationTest {

    private static final String NAMESPACE1 = "value-map-test";
    private static final String NAMESPACE2 = "more-namespace";

    public static final String INT_VAR = "integer-variable";
    public static final String BOOL_VAR = "boolean-variable";
    public static final String STRING_VAR = "string-variable";
    public static final String UNDEFINED_VAR = "undefined-variable";

    private PipelineConfiguration configuration;

    @BeforeEach
    void beforeEach() throws ConfigurationException {
        configuration = new PipelineConfiguration();
        configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, 42);
        configuration.setValue(NAMESPACE1, BOOL_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, true);
        configuration.setValue(NAMESPACE1, STRING_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, "Hello world!");

        configuration.setValue(NAMESPACE2, STRING_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, "This is more stuff!");
        configuration.setValue(null, INT_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, 100);
    }

    @Test
    void getValue() {
        assertEquals(42, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));
        assertEquals(true, configuration.getValue(NAMESPACE1, BOOL_VAR, ExternalResourceType.ANY));
        assertEquals("Hello world!", configuration.getValue(NAMESPACE1, STRING_VAR, ExternalResourceType.ANY));
        assertEquals("This is more stuff!", configuration.getValue(NAMESPACE2, STRING_VAR, ExternalResourceType.ANY));
        assertEquals(100, configuration.getValue(null, INT_VAR, ExternalResourceType.ANY));
    }

    @Test
    void getValueWithExecutionValue() throws ConfigurationException {
        assertEquals(new Integer(-1), configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY, -1));
        configuration.setValue(NAMESPACE1, INT_VAR, GLOBAL, ExternalResourceType.ANY, -2);
        assertEquals(new Integer(-2), configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY, -1));

        assertEquals(new Integer(-1), configuration.getValue(NAMESPACE1, UNDEFINED_VAR, ExternalResourceType.ANY, -1));
        configuration.setValue(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, -2);
        assertEquals(new Integer(-1), configuration.getValue(NAMESPACE1, UNDEFINED_VAR, ExternalResourceType.ANY, -1));
        configuration.setValue(NAMESPACE1, UNDEFINED_VAR, GLOBAL, ExternalResourceType.ANY, -3);
        assertEquals(new Integer(-3), configuration.getValue(NAMESPACE1, UNDEFINED_VAR, ExternalResourceType.ANY, -1));
    }


    @Test
    void getType() {
        assertEquals(Integer.class, configuration.getType(NAMESPACE1, INT_VAR));
        assertEquals(Boolean.class, configuration.getType(NAMESPACE1, BOOL_VAR));
        assertEquals(String.class, configuration.getType(NAMESPACE1, STRING_VAR));
        assertNull(configuration.getType(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void getTypeName() {
        assertEquals("Integer", configuration.getTypeName(NAMESPACE1, INT_VAR));
        assertEquals("Boolean", configuration.getTypeName(NAMESPACE1, BOOL_VAR));
        assertEquals("String", configuration.getTypeName(NAMESPACE1, STRING_VAR));
        assertNull(configuration.getTypeName(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void namespaceSet() {
        Set<String> namespaceSet = configuration.namespaceSet();
        assertNotNull(namespaceSet);
        assertEquals(3, namespaceSet.size());
        assertTrue(namespaceSet.containsAll(Arrays.asList(NAMESPACE1, NAMESPACE2, "global")));
    }

    @Test
    void keySet1() {
        Set<String> keySet = configuration.keySet(NAMESPACE1);
        assertNotNull(keySet);
        assertEquals(3, keySet.size());
        assertTrue(keySet.containsAll(Arrays.asList(INT_VAR, BOOL_VAR, STRING_VAR)));
    }

    @Test
    void keySet2() {
        Set<String> keySet = configuration.keySet(NAMESPACE2);
        assertNotNull(keySet);
        assertEquals(1, keySet.size());
        assertTrue(keySet.contains(STRING_VAR));
    }

    @Test
    void keySet3() {
        Set<String> keySet = configuration.keySet(null);
        assertNotNull(keySet);
        assertEquals(1, keySet.size());
        assertTrue(keySet.contains(INT_VAR));
    }

    @Test
    void keySet4() {
        Set<String> keySet = configuration.keySet("unknown-to-anybody");
        assertNotNull(keySet);
        assertTrue(keySet.isEmpty());
    }

    @Test
    void setValue() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, 100);
        assertEquals(100, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));
    }

    @Test
    void setValueWithHigherRank() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, INT_VAR, GLOBAL, ExternalResourceType.ANY, 100);
        assertEquals(100, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));
    }

    @Test
    void setValueWithLowerRank() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, 100);
        configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, 200);
        assertEquals(100, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));
    }

    @Test
    void setValueWithWrongType() {
        assertThrows(ConfigurationException.class, () -> configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, "something"));
    }

    @Test
    void setNullValue() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, STRING_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, null);
        assertNull(configuration.getValue(NAMESPACE1, STRING_VAR, ExternalResourceType.ANY));
    }

    @Test
    void setNullValueForUndefined() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, UNDEFINED_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, null);
        assertNull(configuration.getValue(NAMESPACE1, UNDEFINED_VAR, ExternalResourceType.ANY));
    }

    @Test
    void setType() throws ConfigurationException {
        assertNull(configuration.getType(NAMESPACE1, UNDEFINED_VAR));

        configuration.setType(NAMESPACE1, UNDEFINED_VAR, List.class);
        assertEquals(List.class, configuration.getType(NAMESPACE1, UNDEFINED_VAR));

        configuration.setType(NAMESPACE1, UNDEFINED_VAR, List.class);
        assertEquals(List.class, configuration.getType(NAMESPACE1, UNDEFINED_VAR));

        assertThrows(ConfigurationException.class, () -> configuration.setType(NAMESPACE1, UNDEFINED_VAR, Integer.class));
        assertEquals(List.class, configuration.getType(NAMESPACE1, UNDEFINED_VAR));

        configuration.setType(NAMESPACE1, UNDEFINED_VAR, List.class);
        assertEquals(List.class, configuration.getType(NAMESPACE1, UNDEFINED_VAR));

        configuration.setType(NAMESPACE1, UNDEFINED_VAR, List.class);
        assertEquals(List.class, configuration.getType(NAMESPACE1, UNDEFINED_VAR));
    }


    @Test
    void setKeyComplete() {
        configuration.setKeyComplete(NAMESPACE1);
        assertThrows(ConfigurationException.class, () -> configuration.setValue(NAMESPACE1, "something-new", PIPELINE_EXECUTION, ExternalResourceType.ANY, 300));
        assertDoesNotThrow(() -> configuration.setValue(NAMESPACE2, "something-new", PIPELINE_EXECUTION, ExternalResourceType.ANY, 300));
    }

    @Test
    void setReadOnly() throws ConfigurationException {
        configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, 200);
        assertEquals(200, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));

        configuration.setReadOnly();
        assertThrows(ConfigurationException.class, () -> configuration.setValue(NAMESPACE1, INT_VAR, PIPELINE_EXECUTION, ExternalResourceType.ANY, 300));
        assertEquals(200, configuration.getValue(NAMESPACE1, INT_VAR, ExternalResourceType.ANY));

        assertThrows(ConfigurationException.class, () -> configuration.setType(NAMESPACE1, UNDEFINED_VAR, List.class));
        assertNull(configuration.getType(NAMESPACE1, UNDEFINED_VAR));
    }

    @Test
    void isValidType() {
        assertTrue(configuration.isValidType(NAMESPACE1, INT_VAR, null));
        assertTrue(configuration.isValidType(NAMESPACE1, INT_VAR, 100));
        assertFalse(configuration.isValidType(NAMESPACE1, INT_VAR, "Hallo"));

        assertTrue(configuration.isValidType(NAMESPACE1, BOOL_VAR, null));
        assertTrue(configuration.isValidType(NAMESPACE1, BOOL_VAR, true));
        assertFalse(configuration.isValidType(NAMESPACE1, BOOL_VAR, 100));

        assertTrue(configuration.isValidType(NAMESPACE1, STRING_VAR, null));
        assertTrue(configuration.isValidType(NAMESPACE1, STRING_VAR, "more"));
        assertFalse(configuration.isValidType(NAMESPACE1, STRING_VAR, 100));

        assertTrue(configuration.isValidType(NAMESPACE1, UNDEFINED_VAR, null));
        assertTrue(configuration.isValidType(NAMESPACE1, UNDEFINED_VAR, "more"));
        assertTrue(configuration.isValidType(NAMESPACE1, UNDEFINED_VAR, 100));
        assertTrue(configuration.isValidType(NAMESPACE1, UNDEFINED_VAR, new StringReader("one, two, three")));
    }

    @Test
    @Disabled("feature not yet implemented")
    void isValidTypeForSubtypes() throws ConfigurationException {
        configuration.setType(NAMESPACE1, UNDEFINED_VAR, Reader.class);
        assertTrue(configuration.isValidType(NAMESPACE1, UNDEFINED_VAR, new StringReader("one, two, three")));
    }

    @Test
    @Disabled("feature not yet implemented")
    void setValueForSubtypes() throws ConfigurationException {
        Reader reader = new StringReader("one, two, three");
        configuration.setType(NAMESPACE1, UNDEFINED_VAR, Reader.class);
        configuration.setValue(NAMESPACE1, UNDEFINED_VAR, PIPELINE_DEFINITION, ExternalResourceType.ANY, reader);
        assertEquals(reader, configuration.getValue(NAMESPACE1, UNDEFINED_VAR, ExternalResourceType.ANY));
    }

}