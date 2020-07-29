package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoaderFactory;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.SystemResourceLoader;

import java.util.HashMap;
import java.util.Map;

public class CoreResourceLoaderFactory implements ResourceLoaderFactory {

    private Map<String, ResourceLoader> resourceLoaderMap = new HashMap<>();

    public CoreResourceLoaderFactory() {
        resourceLoaderMap.put("classpath", new SystemResourceLoader());
        resourceLoaderMap.put("http", new HttpResourceLoader());
    }

    @Override
    public ResourceLoader getResourceLoader(String name) {
        return resourceLoaderMap.get(name);
    }
}
