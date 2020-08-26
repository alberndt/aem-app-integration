package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogAppender;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance> {

    @Nonnull
    private final AppIntegrationFactory<CoreTestAppInstance> factory;

    @Nonnull
    private final Supplier<IntegrationLogAppender> appenderSupplier;

    public CoreTestAppIntegrationEngine(@Nonnull AppIntegrationFactory<CoreTestAppInstance> factory, @Nonnull final Supplier<IntegrationLogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Nonnull
    @Override
    protected AppIntegrationFactory<CoreTestAppInstance> getFactory() {
        return factory;
    }

    @Override
    protected GlobalContext createGlobalContext(@Nonnull final String applicationId, @Nonnull final Application application) {
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = factory.getResourceLoader(resourceLoaderName);
        if (resourceLoader != null) {
            return new CoreTestGlobalContext(resourceLoader, appenderSupplier);
        } else {
            throw new AppIntegrationException(String.format("ResourceLoader %s not found. Cannot create context", resourceLoaderName));
        }
    }
}
