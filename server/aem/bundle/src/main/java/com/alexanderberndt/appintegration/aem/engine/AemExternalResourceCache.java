package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.cache.AbstractExternalResourceCache;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.DataMap;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.jackrabbit.JcrConstants.*;

public class AemExternalResourceCache extends AbstractExternalResourceCache<AemExternalResourceCache.CacheEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String CACHE_ROOT = "/var/aem-app-integration/%s/files";

    public static final int MILLIS_UNTIL_LOCKS_EXPIRE = 5 * 60 * 1000; // 5 minutes to expire

    public static final String LOCK_ATTR = "lock";
    public static final String LOCKED_SINCE_ATTR = "lockedSince";
    public static final String VERSION_ATTR = "version";
    public static final String URI_ATTR = "uri";
    public static final String TYPE_ATTR = "TYPE";
    public static final String JCR_PATH_SEPARATOR = "/";

    @Nonnull
    private final ResourceResolver resolver;

    // root-path, e.g. /var/app-integration/<my-app>/files
    @Nonnull
    private final String rootPath;

    private final Random random = new Random();

    @Nullable
    private String versionId;


    public AemExternalResourceCache(@Nonnull ResourceResolver resolver, @Nonnull String applicationId) {
        this.resolver = resolver;
        this.rootPath = String.format(CACHE_ROOT, applicationId);
    }


    @Override
    public boolean isLongRunningWrite() {
        return (this.versionId != null);
    }


    @Override
    public void startLongRunningWrite(@Nullable String nameHint) {
        this.versionId = null;
        final String tempVersionId = StringUtils.defaultIfBlank(nameHint, Long.toHexString((long) Math.floor(Math.random() * 0x100000000L)));

        runAsTransactionOnRootResValueMap("start long running write", valueMap -> {
            if (canBeLocked(valueMap)) {
                valueMap.put(LOCK_ATTR, tempVersionId);
                valueMap.put(LOCKED_SINCE_ATTR, Calendar.getInstance());
            } else {
                throw new AppIntegrationException("Cannot lock cache at " + rootPath);
            }
        });

        this.versionId = tempVersionId;
    }


    @Override
    public void commitLongRunningWrite() {
        if (this.versionId == null)
            throw new AppIntegrationException("Cannot commit, as no transaction is currently running!");

        runAsTransactionOnRootResValueMap("commit", valueMap -> {
            if (this.versionId.equals(valueMap.get(LOCK_ATTR, String.class))) {
                valueMap.put(VERSION_ATTR, versionId);
                valueMap.remove(LOCK_ATTR);
                valueMap.remove(LOCKED_SINCE_ATTR);
            } else {
                throw new AppIntegrationException("Cannot commit, as currently a different transaction is running!");
            }
        });
    }


    @Override
    public void rollbackLongRunningWrite() {
        if (this.versionId == null)
            throw new AppIntegrationException("Cannot rollback, as no transaction is currently running!");

        runAsTransactionOnRootResValueMap("rollback", valueMap -> {
            final String curLock = valueMap.get(LOCK_ATTR, String.class);
            if (this.versionId.equals(curLock)) {
                valueMap.remove(LOCK_ATTR);
                valueMap.remove(LOCKED_SINCE_ATTR);
            } else {
                throw new AppIntegrationException("Cannot rollback, as currently a different transaction is running!");
            }
        });
    }


    private boolean canBeLocked(ValueMap valueMap) {
        final String currentLockId = valueMap.get(LOCK_ATTR, String.class);

        // is resource not locked yet?
        if (StringUtils.isBlank(currentLockId)) {
            return true;
        }

        // is resource locked by ourselves
        if (StringUtils.equals(this.versionId, currentLockId)) {
            return true;
        }

        // is lock expired?
        final Calendar now = Calendar.getInstance();
        final Calendar lockedSince = valueMap.get(LOCKED_SINCE_ATTR, Calendar.class);
        if (lockedSince != null) {
            final long diff = now.getTimeInMillis() - lockedSince.getTimeInMillis();
            return diff >= MILLIS_UNTIL_LOCKS_EXPIRE;
        } else {
            return true;
        }
    }

    @Override
    protected CacheEntry createNewCacheEntry(ExternalResource resource) {

        final CachePath cachePath = new CachePath(resource.getUri());

        try {
            this.resolver.refresh();
            final Resource cachePathRes = getOrCreateResource(cachePath.getPath());
            final String hashCode = Integer.toHexString(resource.getUri().toString().hashCode());

            // find new cache-entry name
            String entryName;
            int i = 0;
            do {
                if (i++ > 20) {
                    throw new AppIntegrationException("Could NOT create a unique file entry for " + cachePathRes.getPath());
                }
                entryName = hashCode + "_" + Integer.toHexString(random.nextInt(0x1000));
            } while (cachePathRes.getChild(entryName) != null);

            final Resource cacheEntryRes = Objects.requireNonNull(resolver.create(cachePathRes, entryName, null));
            final ModifiableValueMap modifiableValueMap = Objects.requireNonNull(cacheEntryRes.adaptTo(ModifiableValueMap.class));
            modifiableValueMap.put(URI_ATTR, resource.getUri().toString());

            return new CacheEntry(cachePath, cacheEntryRes);

        } catch (PersistenceException e) {
            throw new AppIntegrationException("Cannot create new cache-entry " + cachePath + "!", e);
        }
    }

    @Override
    protected CacheEntry getCacheEntry(URI uri) {

        final Resource rootRes = resolver.getResource(rootPath);
        if (rootRes == null) {
            return null;
        }
        final String curVersion = StringUtils.trimToNull(rootRes.getValueMap().get(VERSION_ATTR, String.class));

        // find existing entry
        final CachePath cachePath = new CachePath(uri);
        final Resource cachePathRes = resolver.getResource(cachePath.getPath());
        if (cachePathRes == null) {
            return null;
        }

        // iterate over all entry
        CacheEntry readThroughEntry = null;
        final Iterator<Resource> cacheResIter = cachePathRes.listChildren();
        while (cacheResIter.hasNext()) {
            final Resource curCacheEntryRes = cacheResIter.next();
            final ValueMap valueMap = curCacheEntryRes.getValueMap();
            if (StringUtils.equals(uri.toString(), valueMap.get(URI_ATTR, String.class))) {
                final String curCacheEntryVersion = valueMap.get(VERSION_ATTR, String.class);
                if (StringUtils.equals(curVersion, curCacheEntryVersion)) {
                    return new CacheEntry(cachePath, curCacheEntryRes);
                }
                if (curCacheEntryVersion == null) {
                    readThroughEntry = new CacheEntry(cachePath, curCacheEntryRes);
                }
            }
        }

        // if nothing with the correct version was found, then return any found read-through entry (without version)
        return readThroughEntry;
    }

    @Override
    protected void addToCurrentLongRunningWrite(CacheEntry cacheEntry) {
        if (StringUtils.isNotBlank(versionId)) {
            final ModifiableValueMap modifiableValueMap = cacheEntry.getCacheEntryValueMap();
            final List<String> versions = Optional.ofNullable(modifiableValueMap.get(VERSION_ATTR, String[].class))
                    .map(Arrays::asList)
                    .map(ArrayList::new)
                    .map(list -> {
                        list.add(versionId);
                        return (List<String>) list;
                    })
                    .orElseGet(() -> Collections.singletonList(versionId));

            modifiableValueMap.put(VERSION_ATTR, versions.toArray(new String[0]));
        }
    }

    @Override
    protected Supplier<InputStream> writeContent(CacheEntry cacheEntry, InputStream contentAsInputStream, String mimeType) {

        try {
            final Resource contentRes = resolver.create(
                    cacheEntry.getCacheEntryRes(),
                    cacheEntry.getCachePath().getFilename(),
                    Collections.singletonMap(JCR_PRIMARYTYPE, NT_FILE));

            Map<String, Object> propertiesMap = new HashMap<>();
            propertiesMap.put(JCR_PRIMARYTYPE, NT_RESOURCE);
            propertiesMap.put(JCR_MIMETYPE, mimeType);
            propertiesMap.put(JCR_DATA, contentAsInputStream);

            resolver.create(contentRes, JCR_CONTENT, propertiesMap);
            // ToDo: Re-check the panic commits. This should follow a real plan (check, that input stream is already processed)
            //resolver.commit();
            return () -> contentRes.adaptTo(InputStream.class);

        } catch (PersistenceException e) {
            throw new AppIntegrationException("Cannot write content to " + cacheEntry.getCachePath().getPath(), e);
        }
    }

    @Override
    protected void writeMetadata(CacheEntry cacheEntry, DataMap metadataMap) {

        try {
            final String nodeName = cacheEntry.getCachePath().getMetadataName();
            Resource metadataRes = cacheEntry.getCacheEntryRes().getChild(nodeName);
            if (metadataRes == null) metadataRes = resolver.create(cacheEntry.getCacheEntryRes(), nodeName, null);

            final ModifiableValueMap valueMap = Objects.requireNonNull(metadataRes.adaptTo(ModifiableValueMap.class));

            Set<String> keySet = valueMap.keySet().stream()
                    .filter(key -> !StringUtils.startsWithAny(key, "jcr:", "cq:"))
                    .collect(Collectors.toSet());
            keySet.forEach(valueMap::remove);

            metadataMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().startsWith("jcr:"))
                    .filter(entry -> !entry.getKey().startsWith("cq:"))
                    .filter(entry -> (entry.getValue() instanceof String) || (entry.getValue() instanceof Number))
                    .forEach(entry -> valueMap.put(entry.getKey(), entry.getValue()));

        } catch (PersistenceException e) {
            throw new AppIntegrationException("Cannot write content to " + cacheEntry.getCachePath().getPath(), e);
        }

    }

    @Override
    protected void commitCacheEntry(CacheEntry cacheEntry) {

        if (StringUtils.isNotBlank(versionId)) {
            final Resource rootRes = resolver.getResource(rootPath);
            if (rootRes != null) {
                final ModifiableValueMap valueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));
                final String repositoryLock = valueMap.get(LOCK_ATTR, String.class);
                if (versionId.equals(repositoryLock)) {
                    valueMap.put(LOCKED_SINCE_ATTR, Calendar.getInstance());
                } else {
                    throw new AppIntegrationException("Cannot write cache, as it is locked by different long-running processes!" +
                            " (was: " + repositoryLock + ", expected: " + versionId + ")");
                }
            } else {
                LOG.warn("Cannot refresh lock on {}, because path not found!", rootPath);
            }
        }

        try {
            resolver.commit();
        } catch (PersistenceException e) {
            throw new AppIntegrationException("Cannot commit cache entry " + cacheEntry.getCachePath().getPath(), e);
        }
    }


    @Override
    @Nullable
    public ExternalResource getCachedResource(@Nonnull URI uri, @Nonnull ExternalResourceFactory resourceFactory) {

        final CacheEntry cacheEntry = getCacheEntry(uri);
        if (cacheEntry != null) {
            final Resource cacheEntryRes = cacheEntry.getCacheEntryRes();
            final ValueMap cacheEntryValueMap = cacheEntryRes.getValueMap();

            final ExternalResourceType type = ExternalResourceType.parse(cacheEntryValueMap.get(TYPE_ATTR, String.class));

            final Resource contentRes = Objects.requireNonNull(cacheEntryRes.getChild(cacheEntry.getCachePath().getFilename()));
            final InputStream content = Objects.requireNonNull(contentRes.adaptTo(InputStream.class));

            final Resource metadataRes = Objects.requireNonNull(cacheEntryRes.getChild(cacheEntry.getCachePath().getMetadataName()));
            final ValueMap metadataValueMap = metadataRes.getValueMap();

            final DataMap metadataMap = new DataMap();
            metadataValueMap.entrySet().stream()
                    .filter(entry -> !StringUtils.startsWith(entry.getKey(), "jcr:"))
                    .filter(entry -> !StringUtils.startsWith(entry.getKey(), "cq:"))
                    .forEach(entry -> metadataMap.setData(entry.getKey(), entry.getValue()));

            return resourceFactory.createExternalResource(uri, type, content, metadataMap);

        } else {
            return null;
        }
    }

    private void runAsTransactionOnRootResValueMap(@Nonnull String methodName, Consumer<ModifiableValueMap> consumer) {
        try {
            resolver.refresh();
            final Resource rootRes = getOrCreateResource(rootPath);
            final ModifiableValueMap modifiableValueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));
            consumer.accept(modifiableValueMap);
            resolver.commit();
        } catch (PersistenceException e) {
            throw new AppIntegrationException("Failed to " + methodName + " on " + rootPath);
        }
    }

    @Nonnull
    protected Resource getOrCreateResource(String path) throws PersistenceException {
        final String[] splitPath = path.split(JCR_PATH_SEPARATOR);
        if ((splitPath.length < 3) || (!splitPath[0].equals(""))) {
            throw new AppIntegrationException("Cannot create path " + path + "! Must be an absolute path with at least two levels");
        }

        // handle top-level path-elem
        final String topLevelPath = JCR_PATH_SEPARATOR + splitPath[1] + JCR_PATH_SEPARATOR + splitPath[2];
        Resource curResource = resolver.getResource(topLevelPath);
        if (curResource == null) {
            throw new AppIntegrationException("Cannot create path " + path + ", as top-level paths should already exists: " + topLevelPath);
        }

        // handle 2nd-level path-elements and deeper
        for (int i = 3; i < splitPath.length; i++) {
            curResource = getOrCreateChild(curResource, splitPath[i]);
        }

        return curResource;
    }

    @Nonnull
    private Resource getOrCreateChild(@Nonnull Resource parentRes, @Nonnull String childName) throws PersistenceException {
        Resource childRes = parentRes.getChild(childName);
        if (childRes == null) {
            childRes = resolver.create(parentRes, childName, Collections.singletonMap(JCR_PRIMARYTYPE, NT_UNSTRUCTURED));
        }
        return childRes;
    }


    private class CachePath {

        private static final String METADATA_NAME = "metadata";

        private final String path;

        private final String filename;

        public CachePath(URI uri) {
            List<String> splitPath = Arrays.stream(StringUtils.split(uri.getPath(), "/"))
                    .map(StringUtils::trimToNull)
                    .filter(Objects::nonNull)
                    .map(JcrUtil::escapeIllegalJcrChars)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (splitPath.isEmpty()) splitPath.add("index");
            this.filename = splitPath.get(splitPath.size() - 1);

            if (StringUtils.isNotBlank(uri.getQuery())) splitPath.add(Integer.toHexString(uri.getQuery().hashCode()));
            this.path = rootPath + JCR_PATH_SEPARATOR + String.join(JCR_PATH_SEPARATOR, splitPath);
        }

        public String getPath() {
            return path;
        }

        public String getFilename() {
            return filename;
        }

        public String getMetadataName() {
            return METADATA_NAME + (METADATA_NAME.equals(filename) ? "0" : "");
        }
    }


    protected static class CacheEntry {

        private final CachePath cachePath;

        private final Resource cacheEntryRes;

        private ModifiableValueMap cacheEntryValueMap;

        public CacheEntry(CachePath cachePath, Resource cacheEntryRes) {
            this.cachePath = cachePath;
            this.cacheEntryRes = cacheEntryRes;
        }

        public Resource getCacheEntryRes() {
            return cacheEntryRes;
        }

        public CachePath getCachePath() {
            return cachePath;
        }

        public ModifiableValueMap getCacheEntryValueMap() {
            if (cacheEntryValueMap == null) {
                cacheEntryValueMap = Objects.requireNonNull(cacheEntryRes.adaptTo(ModifiableValueMap.class));
            }
            return cacheEntryValueMap;
        }
    }
}
