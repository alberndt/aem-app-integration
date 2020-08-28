package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;

@Component(service=AemAppIntegrationEngine.class)
public class AemAppIntegrationEngine extends AppIntegrationEngine<SlingApplicationInstance> {

    @Reference
    private AemAppIntegrationFactory factory;

    @Override
    protected AppIntegrationFactory<SlingApplicationInstance> getFactory() {
        return factory;
    }

    @Override
    protected GlobalContext createGlobalContext(@Nonnull String applicationId, @Nonnull Application application) {
        return null;
    }
}
