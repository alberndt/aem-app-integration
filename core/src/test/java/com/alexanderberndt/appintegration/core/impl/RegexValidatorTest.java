package com.alexanderberndt.appintegration.core.impl;

import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;
import org.junit.Test;

public class RegexValidatorTest {

    private static final String JSON1 = "{\"regex\":\"hello world\"}";

    @Test
    public void filter() {

        IntegrationTaskDef taskDef = IntegrationTaskTestUtil
                .buildTaskDef(RegexValidator.TASK_NAME)
                .property(RegexValidator.REGEX_PARAM, "hello")
                .build();

        RegexValidator regexValidator = new RegexValidator();
        regexValidator.setup(taskDef);
        regexValidator.filter("Here is my hello world!", null);

    }


}