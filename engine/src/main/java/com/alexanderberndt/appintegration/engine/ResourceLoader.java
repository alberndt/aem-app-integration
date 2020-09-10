package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface ResourceLoader {

    // ToDo: Handle Metadata

    ExternalResource load(@Nonnull final ExternalResourceRef resourceRef, @Nonnull final ExternalResourceFactory factory) throws IOException;

    @Deprecated
    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl) throws URISyntaxException {
        return resolveAbsoluteUrl(absoluteUrl, ExternalResourceType.ANY);
    }

    @Deprecated
    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl, ExternalResourceType expectedType) throws URISyntaxException {
        final URI uri = new URI(absoluteUrl);
        return new ExternalResourceRef(uri, expectedType);
    }

    @Deprecated
    default ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl) throws URISyntaxException {
        return resolveRelativeUrl(baseResource, relativeUrl, ExternalResourceType.ANY);
    }

    @Deprecated
    default ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl, ExternalResourceType expectedType) throws URISyntaxException {
        return resolveRelativeUrl(baseResource.getUri(), relativeUrl, expectedType);
    }

    @Deprecated
    ExternalResourceRef resolveRelativeUrl(final URI baseUri, final String relativeUrl, ExternalResourceType expectedType) throws URISyntaxException;


}
