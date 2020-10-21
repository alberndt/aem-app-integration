
package com.alexanderberndt.appintegration.engine.cache;

import com.alexanderberndt.appintegration.engine.cache.impl.HashingInputStream;
import com.alexanderberndt.appintegration.engine.cache.impl.ReadAheadInputStream;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Supplier;

public abstract class AbstractExternalResourceCache<T> implements ExternalResourceCache {

    protected abstract T createNewCacheEntry(ExternalResource resource);

    protected abstract T getCacheEntry(URI uri);

    protected abstract void addToCurrentLongRunningWrite(T cacheEntry);

    protected abstract Supplier<InputStream> writeContent(T cacheEntry, InputStream contentAsInputStream, String mimeType);

    protected abstract void writeMetadata(T cacheEntry, DataMap metadataMap);

    protected abstract void commitCacheEntry(T cacheEntry);


    @Nonnull
    @Override
    public final ExternalResource storeResource(@Nonnull ExternalResource resource) {

        // check, if resource-loader decided for the cached version
        final T existingCacheEntry = getCacheEntry(resource.getUri());

        if ((existingCacheEntry != null) && (resource.getLoadStatus() == ExternalResource.LoadStatus.CACHED)) {
            addToCurrentLongRunningWrite(existingCacheEntry);
            writeMetadata(existingCacheEntry, resource.getMetadataMap());
            commitCacheEntry(existingCacheEntry);
            return resource;
        }

        // check, if resource has not changed
        final HashingInputStream hashingInputStream = new HashingInputStream(resource.getContentAsInputStream());
        final ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(hashingInputStream);


        // ToDo: Load cached resource first
//        if ((existingCacheEntry != null) && isContentNotModified(readAheadInputStream, hashingInputStream, resource.getMetadataMap())) {
//            addToCurrentLongRunningWrite(existingCacheEntry);
//            writeMetadata(existingCacheEntry, resource.getMetadataMap());
//            commitCacheEntry(existingCacheEntry);
//            return cachedResource;
//        }

        // store as new version
        final T newCacheEntry = createNewCacheEntry(resource);
        addToCurrentLongRunningWrite(newCacheEntry);
        // ToDo: Get correct mime-type
        Supplier<InputStream> inputStreamSupplier = writeContent(newCacheEntry, resource.getContentAsInputStream(), "text/plain");
        resource.setMetadata("SHA-256", hashingInputStream.getHashString());
        resource.setMetadata("size", 100);

        writeMetadata(newCacheEntry, resource.getMetadataMap());

        commitCacheEntry(newCacheEntry);
        resource.setContentSupplier(inputStreamSupplier, InputStream.class);
        return resource;
    }

    private boolean isContentNotModified(ReadAheadInputStream readAheadInputStream, HashingInputStream hashingInputStream, @Nonnull DataMap cachedMetaData) {
        final String cachedSha256 = cachedMetaData.getData("SHA-256", String.class);
        final Integer cachedSize = cachedMetaData.getData("size", Integer.class);

        if (StringUtils.isNotBlank(cachedSha256) && (cachedSize != null) && (cachedSize >= 0) && (cachedSize < (768 * 1024))) {
            readAheadInputStream.readAhead(768 * 1024);
            if (readAheadInputStream.isInputFullyRead()) {
                final String sha256 = hashingInputStream.getHashString();
                return StringUtils.isNotBlank(sha256) && StringUtils.equals(sha256, cachedSha256);
            }
        }

        return false;
    }


}