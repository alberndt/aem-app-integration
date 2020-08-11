package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;

public class CoreAppIntegrationEngine extends AppIntegrationEngine<CoreApplicationInstance> {

    private final AppIntegrationFactory<CoreApplicationInstance> factory;

    public CoreAppIntegrationEngine(AppIntegrationFactory<CoreApplicationInstance> factory) {
        this.factory = factory;
    }

    @Override
    protected AppIntegrationFactory<CoreApplicationInstance> getFactory() {
        return factory;
    }
}
