package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import org.apache.commons.lang3.StringUtils;

public class SystemResourceLoader implements ResourceLoader {

    @Override
    public ExternalResource load(ExternalResourceRef resourceRef) {
        final ExternalResource resource = new ExternalResource(this, resourceRef);
        // ToDo: Handle not found
        resource.setContent(ClassLoader.getSystemResourceAsStream(resourceRef.getUrl()));
        return resource;
    }

    @Override
    public ExternalResourceRef resolveRelativeUrl(String baseUrl, String relativeUrl, ExternalResourceType expectedType) {
        final String url;
        if (!StringUtils.contains(baseUrl, '/') || StringUtils.startsWith(relativeUrl, "/")) {
            url = trimSlashes(relativeUrl);
        } else {
            String basePath = StringUtils.substringBeforeLast(baseUrl, "/");
            url = trimSlashes(basePath) + "/" + trimSlashes(relativeUrl);
        }
        return resolveAbsoluteUrl(url, expectedType);
    }


    private String trimSlashes(String input) {
        String temp = input;
        temp = StringUtils.removeStart(temp, "/");
        temp = StringUtils.removeEnd(temp, "/");
        return temp;
    }

}
