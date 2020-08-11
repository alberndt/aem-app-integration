package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.api.Application;
import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.SystemResourceLoader;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreAppIntegrationFactory implements AppIntegrationFactory<CoreApplicationInstance> {

    public static final String SYSTEM_RESOURCE_LOADER_NAME = "classpath";
    public static final String HTTP_RESOURCE_LOADER_NAME = "http";

    public static final List<String> CORE_CONTEXT_PROVIDERS = Collections.singletonList("instance");


    private final Map<String, Application> applicationMap = new HashMap<>();

    private static final Map<String, ResourceLoader> resourceLoaderMap = new HashMap<>();


    static {
        resourceLoaderMap.put(SYSTEM_RESOURCE_LOADER_NAME, new SystemResourceLoader());
        resourceLoaderMap.put(HTTP_RESOURCE_LOADER_NAME, new HttpResourceLoader());
    }

    private static final Map<String, ContextProvider<CoreApplicationInstance>> contextProviderMap = new HashMap<>();

    static {
        contextProviderMap.put("instance", CoreApplicationInstance::getContextMap);
    }


    @Override
    public Map<String, Application> getAllApplications() {
        return Collections.unmodifiableMap(applicationMap);
    }

    @Override
    public Application getApplication(@Nonnull String id) {
        return applicationMap.get(id);
    }

    @Override
    public ResourceLoader getResourceLoader(String id) {
        return resourceLoaderMap.get(id);
    }

    @Override
    public ContextProvider<CoreApplicationInstance> getContextProvider(String id) {
        return contextProviderMap.get(id);
    }

    public void registerApplication(@Nonnull String id, @Nonnull Application application) {
        applicationMap.put(id, application);
    }

    public void unregisterApplication(@Nonnull String id) {
        applicationMap.remove(id);
    }
}
