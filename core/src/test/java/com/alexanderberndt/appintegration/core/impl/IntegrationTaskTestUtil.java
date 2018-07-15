package com.alexanderberndt.appintegration.core.impl;

import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class IntegrationTaskTestUtil {

    private IntegrationTaskTestUtil() {
    }

    public static PropertiesBuilder buildTaskDef(String taskName) {
        return new PropertiesBuilder(taskName);
    }

    public static class PropertiesBuilder {

        private String taskName;

        private Properties properties;

        private List<IntegrationTaskDef> subTasks;

        public PropertiesBuilder(String taskName) {
            this.taskName = taskName;
        }

        public PropertiesBuilder property(String propName, Object propValue) {
            if (this.properties == null) {
                this.properties = new Properties();
            }
            this.properties.put(propName, propValue);
            return this;
        }

        public PropertiesBuilder subtask(IntegrationTaskDef subtask) {
            if (this.subTasks == null) {
                this.subTasks = new ArrayList<>();
            }
            this.subTasks.add(subtask);
            return this;
        }

        public IntegrationTaskDef build() {
            return new IntegrationTaskDef(taskName, properties, subTasks);
        }
    }
}
