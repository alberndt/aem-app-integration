package com.alexanderberndt.appintegration.engine.resources.loader;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;

import java.io.IOException;

public interface ResourceLoader {

    // ToDo: Handle Metadata

    ExternalResource load(final ExternalResourceRef resourceRef) throws IOException;

    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl) {
        return resolveAbsoluteUrl(absoluteUrl, ExternalResourceType.UNKNOWN);
    }

    default ExternalResourceRef resolveAbsoluteUrl(final String absoluteUrl, ExternalResourceType expectedType) {
        return new ExternalResourceRef(absoluteUrl, expectedType);
    }

    default ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl) {
        return resolveRelativeUrl(baseResource, relativeUrl, ExternalResourceType.UNKNOWN);
    }

    ExternalResourceRef resolveRelativeUrl(final ExternalResource baseResource, final String relativeUrl, ExternalResourceType expectedType);


}
