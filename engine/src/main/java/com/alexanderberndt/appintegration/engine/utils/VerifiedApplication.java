package com.alexanderberndt.appintegration.engine.utils;

import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;

import javax.annotation.Nonnull;
import java.util.Objects;

public class VerifiedApplication {

    @Nonnull
    private final String applicationId;

    @Nonnull
    private final Application application;

    @Nonnull
    private final ResourceLoader resourceLoader;

    public VerifiedApplication(@Nonnull String applicationId, @Nonnull Application application, @Nonnull ResourceLoader resourceLoader) {
        this.applicationId = applicationId;
        this.application = application;
        this.resourceLoader = resourceLoader;
    }

    public static VerifiedApplication verify(@Nonnull String applicationId, @Nonnull AppIntegrationFactory<?, ?> factory) {
        final Application application = Objects.requireNonNull(factory.getApplication(applicationId),
                () -> String.format("Application %s is undefined", applicationId));
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = Objects.requireNonNull(factory.getResourceLoader(resourceLoaderName),
                () -> String.format("ResourceLoader %s for application %s is not available!",
                        resourceLoaderName, applicationId));

        return new VerifiedApplication(applicationId, application, resourceLoader);
    }

    @Nonnull
    public String getApplicationId() {
        return applicationId;
    }

    @Nonnull
    public Application getApplication() {
        return application;
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
