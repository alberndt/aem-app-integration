package com.alexanderberndt.appintegration.core.impl;

import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationJob;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResource;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResourceType;
import com.alexanderberndt.appintegration.IntegrationResourceImpl;
import com.alexanderberndt.appintegration.tasks.RegexValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class RegexValidatorTest {

    @Mock
    private IntegrationJob job;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void filter() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(RegexValidator.REGEX_PARAM, "hello world");

        RegexValidator regexValidator = new RegexValidator();

        regexValidator.setupTask(properties);
        IntegrationResource resource = IntegrationResourceImpl.create(IntegrationResourceType.PLAIN_TEXT, "Here is my hello world!");
        regexValidator.execute(resource, job);

        verify(job, times(1)).addWarning(anyString(), ArgumentMatchers.<Object>any());
    }
}