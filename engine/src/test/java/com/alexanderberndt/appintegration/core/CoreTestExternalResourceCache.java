package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CoreTestExternalResourceCache implements ExternalResourceCache {

    private String readVersionId;

    private String writeVersionId;

    private final Map<CacheKey, CacheValue> resourceCache = new HashMap<>();


    @Override
    public void storeResource(@Nonnull ExternalResource resource) {
        final CacheKey key = new CacheKey(resource.getUri(), this.writeVersionId);

        final ByteArrayOutputStream tempContent = new ByteArrayOutputStream();
        try {
            IOUtils.copy(resource.getContentAsInputStream(), tempContent);
        } catch (IOException e) {
            throw new AppIntegrationException("Couldn't read content of resource", e);
        }
        final CacheValue value = new CacheValue(tempContent.toByteArray(), new DataMap(resource.getMetadataMap()));

        resourceCache.put(key, value);
    }

    @Override
    public void markResourceRefreshed(@Nonnull ExternalResource resource) {
        storeResource(resource);
    }

    @Nullable
    @Override
    public ExternalResource getCachedResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory resourceFactory) {
        CacheValue value = resourceCache.get(new CacheKey(resourceRef.getUri(), this.readVersionId));
        if (value == null) {
            value = resourceCache.get(new CacheKey(resourceRef.getUri(), null));
            if (value == null) {
                return null;
            }
        }
        return resourceFactory.createExternalResource(resourceRef, new ByteArrayInputStream(value.content), value.metadata);
    }

    @Override
    public boolean startLongRunningWrite(@Nullable String nameHint) {
        if (this.writeVersionId == null) {
            this.writeVersionId = nameHint;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void continueLongRunningWrite() {
        if (this.writeVersionId == null) {
            throw new AppIntegrationException("Cannot continue long-running write, as it was not started yet.");
        }
    }

    @Override
    public void commitLongRunningWrite() {
        if (this.writeVersionId != null) {
            this.readVersionId = this.writeVersionId;
            this.writeVersionId = null;
        } else {
            throw new AppIntegrationException("Cannot commit long-running write, as it was not started yet.");
        }
    }

    @Override
    public void rollbackLongRunningWrite() {
        if (this.writeVersionId != null) {
            this.writeVersionId = null;
        } else {
            throw new AppIntegrationException("Cannot rollback long-running write, as it was not started yet.");
        }
    }

    private static class CacheKey {

        @Nonnull
        private final URI uri;

        @Nullable
        private final String versionId;

        public CacheKey(@Nonnull URI uri, @Nullable String versionId) {
            this.uri = uri;
            this.versionId = versionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return uri.equals(cacheKey.uri) &&
                    Objects.equals(versionId, cacheKey.versionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, versionId);
        }
    }

    private static class CacheValue {

        @Nonnull
        private final byte[] content;

        @Nonnull
        private final DataMap metadata;

        public CacheValue(@Nonnull byte[] content, @Nonnull DataMap metadata) {
            this.content = content;
            this.metadata = metadata;
        }
    }
}
