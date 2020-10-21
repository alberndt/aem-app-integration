package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.cache.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestExternalResourceCache implements ExternalResourceCache {

    private String readVersionId;

    private String writeVersionId;

    private final Map<CacheKey, CacheValue> resourceCache = new LinkedHashMap<>();

    @Nonnull
    @Override
    public ExternalResource storeResource(@Nonnull ExternalResource resource) {
        final CacheKey key = new CacheKey(resource.getUri(), this.writeVersionId);

        final ByteArrayOutputStream tempContent = new ByteArrayOutputStream();
        try {
            IOUtils.copy(resource.getContentAsInputStream(), tempContent);
        } catch (IOException e) {
            throw new AppIntegrationException("Couldn't read content of resource", e);
        }
        final byte[] content = tempContent.toByteArray();
        final CacheValue value = new CacheValue(content, new DataMap(resource.getMetadataMap()), resource.getType());
        resourceCache.put(key, value);

        resource.setContent(new ByteArrayInputStream(content));
        return resource;
    }

    @Nullable
    @Override
    public ExternalResource getCachedResource(@Nonnull URI uri, @Nonnull ExternalResourceFactory resourceFactory) {
        CacheValue value = resourceCache.get(new CacheKey(uri, this.readVersionId));
        if (value == null) {
            value = resourceCache.get(new CacheKey(uri, null));
            if (value == null) {
                return null;
            }
        }
        return resourceFactory.createExternalResource(uri, value.resourceType, new ByteArrayInputStream(value.content), value.metadata);
    }

    @Override
    public boolean isLongRunningWrite() {
        return (this.writeVersionId != null);
    }

    @Override
    public void startLongRunningWrite(@Nullable String nameHint) {
        if (this.writeVersionId == null) {
            this.writeVersionId = StringUtils.defaultIfBlank(nameHint, "tx");
        } else {
            throw new AppIntegrationException("Cannot start new long-running write - is already running!");
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

    public List<URI> getCacheKeys() {
        return resourceCache.keySet().stream()
                .filter(key -> (key.versionId == null) || StringUtils.equals(readVersionId, key.versionId))
                .map(key -> key.uri)
                .collect(Collectors.toList());
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

        @Nonnull
        private final ExternalResourceType resourceType;

        public CacheValue(@Nonnull byte[] content, @Nonnull DataMap metadata, @Nonnull ExternalResourceType resourceType) {
            this.content = content;
            this.metadata = metadata;
            this.resourceType = resourceType;
        }
    }
}
