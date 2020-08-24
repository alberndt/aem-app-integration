package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;

import java.io.IOException;

public interface ResourceLoader {

    // ToDo: Handle Metadata

    ExternalResource load(final ExternalResourceRef resourceRef) throws IOException;

    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl) {
        return resolveAbsoluteUrl(absoluteUrl, ExternalResourceType.ANY);
    }

    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl, ExternalResourceType expectedType) {
        return new ExternalResourceRef(absoluteUrl, expectedType);
    }

    default ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl) {
        return resolveRelativeUrl(baseResource, relativeUrl, ExternalResourceType.ANY);
    }

    default ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl, ExternalResourceType expectedType) {
        return resolveRelativeUrl(baseResource.getUrl(), relativeUrl, expectedType);
    }

    ExternalResourceRef resolveRelativeUrl(final String baseUrl, final String relativeUrl, ExternalResourceType expectedType);


}
