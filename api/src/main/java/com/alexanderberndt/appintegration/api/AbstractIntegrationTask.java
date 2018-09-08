package com.alexanderberndt.appintegration.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractIntegrationTask implements IntegrationTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationTask.class);

    private final String[] requiredProperties;

    private Map<String, Object> properties;

    private List<IntegrationResourceType> applicableResourceTypes;

    public AbstractIntegrationTask(String... requiredProperties) {
        this.requiredProperties = requiredProperties;
    }

    @Override
    public void setApplicableResourceTypes(List<IntegrationResourceType> applicableResourceTypes) {
        this.applicableResourceTypes = applicableResourceTypes;
    }

    @Override
    public final List<IntegrationResourceType> getApplicableResourceTypes() {
        return this.applicableResourceTypes;
    }

    @Override
    public final void setupTask(Map<String, Object> properties) {

        this.properties = properties;

        if ((requiredProperties != null) && (requiredProperties.length != 0)) {
            if (this.properties == null) {
                LOGGER.error("Missing setupTask-properties: {}", Arrays.asList(requiredProperties));
                throw new IntegrationException("Missing setupTask-properties: " + Arrays.asList(requiredProperties));
            }
            final List<String> missingRequirements = new ArrayList<>();
            for (final String reqProp : requiredProperties) {
                if (!this.properties.containsKey(reqProp)) {
                    missingRequirements.add(reqProp);
                }
            }
            if (!missingRequirements.isEmpty()) {
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
    protected <P> P getProperty(final String propertyName, Class<P> tClass) {
        if (this.properties != null) {
            final Object obj = this.properties.get(propertyName);
            if (obj != null) {
                if (tClass.isInstance(obj)) {
                    return (P) obj;
                } else {
                    LOGGER.warn("Property {} has not the expected type {}, but has actual type {}.",
                            propertyName, tClass, obj.getClass());
                }
            }
        }
        return null;
    }
}
