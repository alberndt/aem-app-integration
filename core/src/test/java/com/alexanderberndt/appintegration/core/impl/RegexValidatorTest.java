package com.alexanderberndt.appintegration.core.impl;

import com.alexanderberndt.appintegration.api.IntegrationContext;
import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class RegexValidatorTest {

    private static final String JSON1 = "{\"regex\":\"hello world\"}";

    @Mock
    private IntegrationContext context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void filter() {

        IntegrationTaskDef taskDef = IntegrationTaskTestUtil
                .buildTaskDef(RegexValidator.TASK_NAME)
                .property(RegexValidator.REGEX_PARAM, "hello")
                .build();

        RegexValidator regexValidator = new RegexValidator();
        regexValidator.setupTask(taskDef);
        regexValidator.execute("Here is my hello world!", context);

        verify(context, times(1)).addWarning(anyString(), anyVararg());

    }


}