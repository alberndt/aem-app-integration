package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;

public class SystemResourceLoader implements ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    @Override
    public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) throws ResourceLoaderException {
        final String resourcePath = StringUtils.removeStart(resourceRef.getUri().normalize().getPath(), "/");
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourcePath);
        if (inputStream != null) {
            return factory.createExternalResource(resourceRef, inputStream);
        } else {
            LOG.warn("Resource {} not found!", resourcePath);
            throw new ResourceLoaderException(ResourceLoaderException.FailedReason.NOT_FOUND, "Resource " + resourcePath + " not found!");
        }
    }

    private static final URI defaultBaseUri;

    static {
        URI tempUri;
        try {
            tempUri = new URI("classpath", "system", null, null);
        } catch (URISyntaxException e) {
            tempUri = null;
        }
        defaultBaseUri = tempUri;
    }

    @Nullable
    @Override
    public URI getDefaultBaseUri() {
        return defaultBaseUri;
    }

}
