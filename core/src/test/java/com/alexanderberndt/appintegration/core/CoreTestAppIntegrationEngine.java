package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance> {

    private final AppIntegrationFactory<CoreTestAppInstance> factory;

    public CoreTestAppIntegrationEngine(AppIntegrationFactory<CoreTestAppInstance> factory) {
        this.factory = factory;
    }

    @Override
    protected AppIntegrationFactory<CoreTestAppInstance> getFactory() {
        return factory;
    }
}
