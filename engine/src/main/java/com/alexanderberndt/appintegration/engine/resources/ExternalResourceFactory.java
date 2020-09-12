package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URI;

public interface ExternalResourceFactory {

    @Nonnull
    ExternalResource createExternalResource(@Nonnull URI uri, @Nullable ExternalResourceType type, @Nonnull InputStream content, @Nullable DataMap metadataMap);

    @Nonnull
    default ExternalResource createExternalResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull InputStream content) {
        return createExternalResource(resourceRef.getUri(), resourceRef.getExpectedType(), content, resourceRef.getMetadataMap());
    }

    @Nonnull
    default ExternalResource createExternalResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull InputStream content, @Nullable DataMap metaDataMap) {
        return createExternalResource(resourceRef.getUri(), resourceRef.getExpectedType(), content, metaDataMap);
    }

}
