package com.alexanderberndt.appintegration.tasks;

import com.alexanderberndt.appintegration.core.CoreTaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.TaskFactory;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreTaskFactoryTest {

    @Test
    void getTask() {
        GenericTask task;
        TaskFactory taskFactory = new CoreTaskFactory();

        task = taskFactory.getTask("properties");
        assertNotNull(task);
        assertTrue(task instanceof PropertiesTask);
    }
}