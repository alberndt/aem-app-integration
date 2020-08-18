package com.alexanderberndt.appintegration.tasks.cache;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryExternalResourcesSet extends AbstractExternalResourcesSet {

    private final ResourceLoader resourceLoader;

    private final String baseUrl;

    private final Map<String, ExternalResource> externalResourceMap = new HashMap<>();

    public InMemoryExternalResourcesSet(ResourceLoader resourceLoader, String baseUrl) {
        this.resourceLoader = resourceLoader;
        this.baseUrl = baseUrl;
    }

    @Override
    protected void prefetch(ExternalResourceRef resourceRef) {
        try {
            ExternalResource resource = resourceLoader.load(resourceRef);
            externalResourceMap.put(resourceRef.getUrl(), resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected ExternalResource getResource(ExternalResourceRef resourceRef, boolean isPrefetched) {
        if (!isPrefetched) {
            prefetch(resourceRef);
        }
        // ToDo: Handle unknown resources, eg. throw exception
        return externalResourceMap.get(resourceRef.getUrl());
    }

    // ToDo: Support A/B Switch

    // ToDo: Support Persistent Cache

    // ToDo: Support Cache Headers
}
