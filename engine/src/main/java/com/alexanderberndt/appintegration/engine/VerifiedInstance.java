package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;

import static com.alexanderberndt.appintegration.helper.ValidationUtil.requireNotNull;

public class VerifiedInstance<I extends ApplicationInstance> {

    @Nonnull
    private final I instance;

    @Nonnull
    private final Application application;

    @Nonnull
    private final ResourceLoader resourceLoader;

    private VerifiedInstance(@Nonnull I instance, @Nonnull Application application, @Nonnull ResourceLoader resourceLoader) {
        this.application = application;
        this.instance = instance;
        this.resourceLoader = resourceLoader;
    }

    public static <I extends ApplicationInstance, C extends GlobalContext> VerifiedInstance<I> verify(@Nonnull I instance, @Nonnull AppIntegrationFactory<I, C> factory) {
        final String applicationId = instance.getApplicationId();
        final Application application = requireNotNull(factory.getApplication(applicationId),
                () -> String.format("Application %s is undefined", applicationId));

        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = requireNotNull(factory.getResourceLoader(resourceLoaderName),
                () -> String.format("ResourceLoader %s for application %s is not available!",
                        resourceLoaderName, applicationId));

        return new VerifiedInstance<>(instance, application, resourceLoader);
    }


    public String getApplicationId() {
        return instance.getApplicationId();
    }

    public String getProcessingPipelineName() {
        return application.getProcessingPipelineName();
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
