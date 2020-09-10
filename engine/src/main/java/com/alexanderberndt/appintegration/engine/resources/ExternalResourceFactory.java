package com.alexanderberndt.appintegration.engine.resources;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface ExternalResourceFactory {

    @Nonnull
    ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, Map<String, Object> metadataMap);

    @Nonnull
    default ExternalResource createExternalResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull InputStream content) {
        // ToDo: Rethink meta-data for resource refs?
        return createExternalResource(resourceRef.getUri(), resourceRef.getExpectedType(), content, null);

    }

}
