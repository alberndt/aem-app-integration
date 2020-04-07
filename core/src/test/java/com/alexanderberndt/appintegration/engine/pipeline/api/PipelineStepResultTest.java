package com.alexanderberndt.appintegration.engine.pipeline.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineStepResultTest {

    @Test
    void addWarning() {
        IntegrationStepResult result = new IntegrationStepResult();
        assertNull(result.getStatus());
        assertNotNull(result.getMessageList());
        assertEquals(0, result.getMessageList().size());

        result.addWarning("Any warning");
        assertEquals(result.getStatus(), IntegrationStepResult.Status.WARNING);
        assertEquals(result.getMessageList().size(), 1);
        List<IntegrationMessage> messageList = result.getMessageList();
        assertEquals("Any warning", messageList.get(0).getMessage());

        result.addWarning("Another warning {}/{}", 1, 2);
        assertEquals(result.getStatus(), IntegrationStepResult.Status.WARNING);
        assertEquals(result.getMessageList().size(), 2);
        messageList = result.getMessageList();
        assertEquals("Another warning 1/2", messageList.get(1).getMessage());

        result.addWarning("More warning {}/{}", "Alex", true, new RuntimeException());
        assertEquals(result.getStatus(), IntegrationStepResult.Status.WARNING);
        assertEquals(result.getMessageList().size(), 3);
        messageList = result.getMessageList();
        assertEquals("More warning Alex/true", messageList.get(2).getMessage());
    }

    @Test
    void addError() {
    }

    @Test
    void getStatus() {
    }

    @Test
    void setStatus() {
    }

    @Test
    void getResult() {
    }

    @Test
    void setResult() {
    }
}