package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestApplicationInstance> {

    private final AppIntegrationFactory<CoreTestApplicationInstance> factory;

    public CoreTestAppIntegrationEngine(AppIntegrationFactory<CoreTestApplicationInstance> factory) {
        this.factory = factory;
    }

    @Override
    protected AppIntegrationFactory<CoreTestApplicationInstance> getFactory() {
        return factory;
    }
}
