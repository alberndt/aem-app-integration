package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class SystemResourceLoader implements ResourceLoader {

    @Override
    public ExternalResource load(ExternalResourceRef resourceRef) throws IOException {
        final ExternalResource resource = new ExternalResource(this, resourceRef);
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceRef.getUrl());

        if (inputStream != null) {
            resource.setContent(inputStream);
            return resource;
        } else {
            throw new IOException(String.format("Resource %s not found!", resourceRef.getUrl()));
        }
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
