package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

@Component
public class SystemResourceLoader implements ResourceLoader {

    @Override
    public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) throws IOException {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceRef.getUrl());

        if (inputStream != null) {
            return factory.createExternalResource(inputStream, resourceRef, this);
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
