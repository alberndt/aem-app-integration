package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class VerifiedAppInstance<I extends ApplicationInstance> {

    @Nonnull
    private final I instance;

    @Nonnull
    private final Application application;

    @Nonnull
    private final ResourceLoader resourceLoader;

    private VerifiedAppInstance(@Nonnull I instance, @Nonnull Application application, @Nonnull ResourceLoader resourceLoader) {
        this.application = application;
        this.instance = instance;
        this.resourceLoader = resourceLoader;
    }

    public static <I extends ApplicationInstance> VerifiedAppInstance<I> verify(@Nonnull I instance, @Nonnull AppIntegrationFactory<I> factory) {
        final String applicationId = instance.getApplicationId();
        final Application application = requireNotNull(factory.getApplication(applicationId),
                () -> String.format("Application %s is undefined", applicationId));

        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = requireNotNull(factory.getResourceLoader(resourceLoaderName),
                () -> String.format("ResourceLoader %s for application %s is not available!",
                        resourceLoaderName, applicationId));

        return new VerifiedAppInstance<>(instance, application, resourceLoader);
    }

    @Nonnull
    private static <I> I requireNotNull(I obj, Supplier<String> errorMessageSupplier) {
        if (obj != null) {
            return obj;
        } else {
            throw new AppIntegrationException(errorMessageSupplier.get());
        }
    }

    public String getApplicationId() {
        return instance.getApplicationId();
    }

    public Application getApplication() {
        return application;
    }

    public String getComponentId() {
        return instance.getComponentId();
    }

    public I getInstance() {
        return instance;
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
