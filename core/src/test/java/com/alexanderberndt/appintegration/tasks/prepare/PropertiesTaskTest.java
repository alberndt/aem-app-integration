package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.api.task.PreparationTask;
import com.alexanderberndt.appintegration.core.CoreGlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.TaskContext;
import com.alexanderberndt.appintegration.utils.ValueMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTaskTest {

    private PreparationTask task = new PropertiesTask();

    @Test
    void getName() {
        assertEquals("properties", task.getName());
    }

    @Test
    void prepare() {
        // global value-map
        Map<String, Object> globalProperties = new HashMap<>();
        globalProperties.put("global", "do NOT copy!!!");
        globalProperties.put("properties.predefined", "this SHOULD work!");
        ValueMap globalParams = new ValueMap(globalProperties, false);
        CoreGlobalContext globalContext = new CoreGlobalContext(globalParams);

        // task properties
        Map<String, Object> taskProperties = new HashMap<>();
        taskProperties.put("hello", "Hello World!");
        taskProperties.put("the-number", 42);
        ValueMap taskParams = new ValueMap(globalParams, "properties", taskProperties, false);
        TaskContext context = globalContext.createChildContext("properties", "properties task", taskParams);
        ExternalResourceRef resourceRef = new ExternalResourceRef();

        assertTrue(resourceRef.getProperties().isEmpty());
        assertNotNull(globalProperties.get("global"));

        task.prepare(context, resourceRef);

        assertEquals("Hello World!", resourceRef.getProperties().get("hello"));
        assertEquals(42, resourceRef.getProperties().get("the-number"));
        assertNull(resourceRef.getProperties().get("global"));
        assertEquals("this SHOULD work!", resourceRef.getProperties().get("predefined"));
    }
}