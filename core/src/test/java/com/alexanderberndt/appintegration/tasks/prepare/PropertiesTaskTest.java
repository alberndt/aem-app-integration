package com.alexanderberndt.appintegration.tasks.prepare;

import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesTaskTest {

    private PreparationTask task = new PropertiesTask();

    @Test
    void getName() {
        assertEquals("properties", task.getName());
    }

    @Test
    @Disabled
    void prepare() {
//        // global value-map
//        Map<String, Object> globalProperties = new HashMap<>();
//        globalProperties.put("global", "do NOT copy!!!");
//        globalProperties.put("properties.predefined", "this SHOULD work!");
//        PipelineConfiguration globalParams = new PipelineConfiguration(globalProperties, false);
//        CoreGlobalContext globalContext = new CoreGlobalContext(null, globalParams);
//
//        // task properties
//        Map<String, Object> taskProperties = new HashMap<>();
//        taskProperties.put("hello", "Hello World!");
//        taskProperties.put("the-number", 42);
//        PipelineConfiguration taskParams = new PipelineConfiguration(globalParams, "properties", taskProperties, false);
//        TaskContext context = globalContext.createChildContext("properties", "properties task", taskParams);
//        ExternalResourceRef resourceRef = new ExternalResourceRef();
//
//        assertTrue(resourceRef.getProperties().isEmpty());
//        assertNotNull(globalProperties.get("global"));
//
//        task.prepare(context, resourceRef);
//
//        assertEquals("Hello World!", resourceRef.getProperties().get("hello"));
//        assertEquals(42, resourceRef.getProperties().get("the-number"));
//        assertNull(resourceRef.getProperties().get("global"));
//        assertEquals("this SHOULD work!", resourceRef.getProperties().get("predefined"));
    }
}