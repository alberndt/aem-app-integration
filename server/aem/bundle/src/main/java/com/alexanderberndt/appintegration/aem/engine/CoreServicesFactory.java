package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import org.osgi.framework.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.*;

@Component(immediate = true)
public class CoreServicesFactory {

    private BundleContext bundleContext;

    private ServiceRegistration<ResourceLoader> serviceRegistration;

    @Activate
    public void activate(BundleContext bundleContext, ComponentContext componentContext, Map<String, Object> properties) {
        this.bundleContext = bundleContext;
        //register(new HttpResourceLoader());
        register(new SystemResourceLoader());


    }

    private void register(ResourceLoader resourceLoader) {
        ServiceFactory<ResourceLoader> serviceFactory = new ServiceFactory<ResourceLoader>() {
            @Override
            public ResourceLoader getService(Bundle bundle, ServiceRegistration<ResourceLoader> registration) {
                return resourceLoader;
            }

            @Override
            public void ungetService(Bundle bundle, ServiceRegistration<ResourceLoader> registration, ResourceLoader service) {
                // do nothing
            }
        };

        Map<String, Object> props = new HashMap<>();
        props.put(Constants.SERVICE_PID, resourceLoader.getClass().getName());
        props.put("component.name", resourceLoader.getClass().getName());
        props.put(Constants.SERVICE_DESCRIPTION, "this is " + resourceLoader.getClass().getSimpleName());
        props.put(Constants.SERVICE_VENDOR, "AEM App-Integration Project");
        serviceRegistration = bundleContext.registerService(ResourceLoader.class, serviceFactory, new Hashtable<>(props));

    }

    @Deactivate

    public void deactivate() {

    }

}
