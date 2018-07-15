package com.alexanderberndt.appintegration.api;

import com.alexanderberndt.appintegration.api.definition.IntegrationTaskDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class AbstractIntegrationTask<IN, OUT> implements IntegrationTask<IN, OUT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationTask.class);

    private final Class<IN> inputClass;

    private final Class<OUT> outputClass;

    private final String[] requiredProperties;

    private IntegrationTaskDef taskDef;

    public AbstractIntegrationTask(Class<IN> inputClass, Class<OUT> outputClass, String... requiredProperties) {
        this.inputClass = inputClass;
        this.outputClass = outputClass;
        this.requiredProperties = requiredProperties;
    }

    @Override
    public final Class<IN> getInputClass() {
        return inputClass;
    }

    @Override
    public final Class<OUT> getOutputClass() {
        return outputClass;
    }

    @Override
    public final void setupTask(final IntegrationTaskDef taskDef) {

        this.taskDef = taskDef;
        if (taskDef == null) {
            LOGGER.error("Task definition MUST NOT be null!");
            throw new IntegrationException("Task definition MUST NOT be null!");
        }

        if ((requiredProperties != null) && (requiredProperties.length != 0)) {

            if (taskDef.getProperties() == null) {
                LOGGER.error("Missing setupTask-properties: {}", Arrays.asList(requiredProperties));
                throw new IntegrationException("Missing setupTask-properties: " + Arrays.asList(requiredProperties));
            }

            final List<String> missingRequirements = new ArrayList<>();
            for (final String reqProp : requiredProperties) {
                if (!taskDef.getProperties().containsKey(reqProp)) {
                    missingRequirements.add(reqProp);
                }
            }

            if (missingRequirements.size() > 0) {
                LOGGER.error("Missing setupTask-properties: {}", missingRequirements);
                throw new IntegrationException("Missing setupTask-properties: " + missingRequirements);
            }
        }

        // call postSetup for deriving classes
        this.postSetup();
    }

    protected void postSetup() {
    }

    @SuppressWarnings("unchecked")
    protected <T> T getProperty(final String propertyName, Class<T> tClass) {
        if (this.taskDef.getProperties() != null) {
            final Object obj = this.taskDef.getProperties().get(propertyName);
            if (obj != null) {
                if (tClass.isInstance(obj)) {
                    return (T) obj;
                } else {
                    LOGGER.warn("Property {} has not the expected type {}, but has actual type {}.",
                            propertyName, tClass, obj.getClass());
                }
            }
        }
        return null;
    }
}
