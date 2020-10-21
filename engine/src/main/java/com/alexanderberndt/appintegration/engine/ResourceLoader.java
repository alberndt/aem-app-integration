package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface ResourceLoader {

    @Nonnull
    ExternalResource load(
            @Nonnull final ExternalResourceRef resourceRef,
            @Nonnull final ExternalResourceFactory factory,
            @Nullable final ExternalResource cachedResource) throws IOException, ResourceLoaderException;

    @Nonnull
    default ExternalResource load(@Nonnull final ExternalResourceRef resourceRef, @Nonnull final ExternalResourceFactory factory)
            throws IOException, ResourceLoaderException {
        return load(resourceRef, factory, null);
    }

    @Nullable
    default URI getDefaultBaseUri() {
        return null;
    }

    @Nonnull
    default URI resolveBaseUri(String url) throws URISyntaxException {
        final URI resolvedUri;
        final URI defaultBaseUri = this.getDefaultBaseUri();
        if (defaultBaseUri != null) {
            if (StringUtils.isBlank(defaultBaseUri.getPath())) {
                final URI tmp = new URI(url);
                final String fixedPath = StringUtils.prependIfMissing(tmp.getPath(), "/");
                final URI temp2Uri = new URI(tmp.getScheme(), tmp.getUserInfo(), tmp.getHost(), tmp.getPort(), fixedPath, tmp.getQuery(), tmp.getFragment());
                resolvedUri = defaultBaseUri.resolve(temp2Uri);
            } else {
                resolvedUri = defaultBaseUri.resolve(url);
            }
        } else {
            resolvedUri = new URI(url);
        }
        return resolvedUri.normalize();
    }

}
