package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import org.apache.commons.lang3.StringUtils;

public class SystemResourceLoader implements ResourceLoader {

    @Override
    public ExternalResource load(String baseUrl, ExternalResourceRef resourceRef) {
        final ExternalResource resource = new ExternalResource(resourceRef);
        // ToDo: Handle not found
        final String url = resolveRelativeUrl(baseUrl, resourceRef.getRelativeUrl());
        resource.setInputStream(ClassLoader.getSystemResourceAsStream(url));

        return resource;
    }

    public String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        if (!StringUtils.contains(baseUrl, '/') || StringUtils.startsWith(relativeUrl, "/")) {
            return trimSlashes(relativeUrl);
        } else {
            String basePath = StringUtils.substringBeforeLast(baseUrl, "/");
            return trimSlashes(basePath) + "/" + trimSlashes(relativeUrl);
        }
    }

    private String trimSlashes(String input) {
        String temp = input;
        temp = StringUtils.removeStart(temp, "/");
        temp = StringUtils.removeEnd(temp, "/");
        return temp;
    }
}
