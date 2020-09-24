package com.alexanderberndt.appintegration.aem.engine.model;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Objects;

@Model(adaptables = Resource.class)
public class SlingApplicationInstance implements ApplicationInstance  {

    @Nonnull
    private final Resource resource;

    @Nonnull
    private final String applicationId;

    @Nonnull
    private final String componentId;

    public SlingApplicationInstance(@Self Resource resource, @Named("applicationId") String applicationId, @Named("componentId") String componentId) {
        this.resource = Objects.requireNonNull(resource);
        this.applicationId = Objects.requireNonNull(applicationId);
        this.componentId = Objects.requireNonNull(componentId);
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
