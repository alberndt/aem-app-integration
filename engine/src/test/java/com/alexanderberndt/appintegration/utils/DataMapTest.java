package com.alexanderberndt.appintegration.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataMapTest {

    @Test
    void getAndSetData() {
        DataMap dataMap = new DataMap();
        dataMap.setData("int", 42);
        dataMap.setData("string", "Hello World!");

        assertEquals(42, dataMap.get("int"));
        assertEquals(new Integer(42), dataMap.getData("int", Integer.class));

        assertEquals("Hello World!", dataMap.get("string"));
        assertEquals("Hello World!", dataMap.getData("string", String.class));

        assertNull(dataMap.getData("int", String.class));
        assertNull(dataMap.getData("string", Integer.class));
        assertNull(dataMap.getData("not-there", Object.class));
    }

    @Test
    void removeData() {
        DataMap dataMap = new DataMap();

        dataMap.setData("string", "Hello World!");
        assertTrue(dataMap.containsKey("string"));

        dataMap.setData("string", null);
        assertFalse(dataMap.containsKey("string"));
    }

}