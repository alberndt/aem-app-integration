package com.alexanderberndt.appintegration.engine.utils;

import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ApplicationInstance;
import com.alexanderberndt.appintegration.engine.ResourceLoader;

import javax.annotation.Nonnull;

public class VerifiedInstance<I extends ApplicationInstance> {

    @Nonnull
    private final VerifiedApplication verifiedApplication;

    @Nonnull
    private final I instance;

    public VerifiedInstance(@Nonnull VerifiedApplication verifiedApplication, @Nonnull I instance) {
        this.verifiedApplication = verifiedApplication;
        this.instance = instance;
    }

    public static <I extends ApplicationInstance> VerifiedInstance<I> verify(@Nonnull I instance, @Nonnull AppIntegrationFactory<I, ?> factory) {
        final String applicationId = instance.getApplicationId();
        final VerifiedApplication verifiedApplication = VerifiedApplication.verify(applicationId, factory);
        return new VerifiedInstance<>(verifiedApplication, instance);
    }


    public String getApplicationId() {
        return instance.getApplicationId();
    }

    @Nonnull
    public VerifiedApplication getVerifiedApplication() {
        return verifiedApplication;
    }

    @Nonnull
    public Application getApplication() {
        return verifiedApplication.getApplication();
    }

    public String getComponentId() {
        return instance.getComponentId();
    }

    @Nonnull
    public I getInstance() {
        return instance;
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return verifiedApplication.getResourceLoader();
    }
}
