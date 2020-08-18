package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;

import javax.annotation.Nonnull;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance> {

    private final AppIntegrationFactory<CoreTestAppInstance> factory;

    public CoreTestAppIntegrationEngine(AppIntegrationFactory<CoreTestAppInstance> factory) {
        this.factory = factory;
    }

    @Override
    protected AppIntegrationFactory<CoreTestAppInstance> getFactory() {
        return factory;
    }

    @Override
    protected GlobalContext createGlobalContext(@Nonnull final Application application) {
        final String resourceLoaderName = application.getResourceLoaderName();
        final ResourceLoader resourceLoader = factory.getResourceLoader(resourceLoaderName);
        if (resourceLoader != null) {
            return new CoreTestGlobalContext(resourceLoader);
        } else {
            throw new AppIntegrationException(String.format("ResourceLoader %s not found. Cannot create context", resourceLoaderName));
        }
    }
}
