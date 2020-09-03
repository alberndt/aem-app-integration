package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance, CoreTestGlobalContext> {

    @Nonnull
    private final CoreAppIntegrationFactory factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    public CoreTestAppIntegrationEngine(@Nonnull CoreAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Nonnull
    @Override
    protected AppIntegrationFactory<CoreTestAppInstance, CoreTestGlobalContext> getFactory() {
        return factory;
    }

    @Override
    protected CoreTestGlobalContext createGlobalContext(@Nonnull final String applicationId, @Nonnull final Application application) {
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = factory.getResourceLoader(resourceLoaderName);
        if (resourceLoader != null) {
            return new CoreTestGlobalContext(resourceLoader, appenderSupplier.get());
        } else {
            throw new AppIntegrationException(String.format("ResourceLoader %s not found. Cannot create context", resourceLoaderName));
        }
    }
}
