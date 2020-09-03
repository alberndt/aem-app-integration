package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class SlingApplicationInstance implements ApplicationInstance  {

    private final Resource resource;

    private final String applicationId;

    private final String componentId;

    public SlingApplicationInstance(Resource resource, String applicationId, String componentId) {
        this.resource = resource;
        this.applicationId = applicationId;
        this.componentId = componentId;
    }

    @Nonnull
    public Resource getResource() {
        return resource;
    }

    @Nonnull
    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Nonnull
    @Override
    public String getComponentId() {
        return componentId;
    }

}
