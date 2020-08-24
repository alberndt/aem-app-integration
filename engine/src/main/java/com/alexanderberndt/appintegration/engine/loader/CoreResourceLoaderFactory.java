package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderFactory;

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
