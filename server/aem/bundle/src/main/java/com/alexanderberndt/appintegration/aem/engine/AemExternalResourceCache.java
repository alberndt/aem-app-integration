package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.DataMap;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.jackrabbit.JcrConstants.*;

public class AemExternalResourceCache implements ExternalResourceCache {

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
    public boolean startLongRunningWrite(@Nullable String nameHint) {
        try {
            this.versionId = null;
            final Resource rootRes = getOrCreateResource(rootPath);
            if (canBeLocked(rootRes)) {
                final String tempVersionId = StringUtils.defaultIfBlank(nameHint, Long.toHexString((long) Math.floor(Math.random() * 0x100000000L)));
                final ModifiableValueMap valueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));
                valueMap.put(LOCK_ATTR, nameHint);
                valueMap.put(LOCKED_SINCE_ATTR, Calendar.getInstance());
                resolver.commit();
                this.versionId = tempVersionId;
                return true;
            } else {
                return false;
            }
        } catch (PersistenceException e) {
            return false;
        }
    }


    // ToDo: Make continueLongRunningWrite implicit
    @Override
    public void continueLongRunningWrite() {
        if (versionId == null) {
            throw new AppIntegrationException("Cannot refresh lock, as cache is not locked yet!");
        }

        final Resource rootRes = resolver.getResource(rootPath);
        if (rootRes == null) {
            throw new AppIntegrationException("Cannot refresh lock on " + rootPath + ", because path not found!");
        }

        final ModifiableValueMap valueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));
        if (versionId.equals(valueMap.get(LOCK_ATTR, String.class))) {
            valueMap.put(LOCKED_SINCE_ATTR, Calendar.getInstance());
            try {
                resolver.commit();
            } catch (PersistenceException e) {
                throw new AppIntegrationException("Cannot refresh lock on " + rootPath, e);
            }
        }
    }

    @Override
    public void commitLongRunningWrite() {
        final Resource rootRes = resolver.getResource(rootPath);
        if (rootRes == null) {
            throw new AppIntegrationException("cannot find root-path " + rootPath + " to set active version");
        }
        final String curVersion = rootRes.getValueMap().get(VERSION_ATTR, String.class);
        final ModifiableValueMap modifiableValueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));

        if (!StringUtils.equals(curVersion, versionId)) {
            if (StringUtils.isNotBlank(versionId)) {
                modifiableValueMap.put(VERSION_ATTR, versionId);
            } else {
                modifiableValueMap.remove(VERSION_ATTR);
            }
        }
        modifiableValueMap.remove(LOCK_ATTR);
        modifiableValueMap.remove(LOCKED_SINCE_ATTR);

        try {
            resolver.commit();
        } catch (PersistenceException e) {
            throw new AppIntegrationException("Cannot commit long-running write", e);
        }
    }


    @Override
    public void rollbackLongRunningWrite() {

        if (versionId == null) {
            throw new AppIntegrationException("Cannot rollback long-running write, as long-running write was not started yet!");
        }

        final Resource rootRes = resolver.getResource(rootPath);
        if (rootRes != null) {
            final ModifiableValueMap valueMap = Objects.requireNonNull(rootRes.adaptTo(ModifiableValueMap.class));
            if (this.versionId.equals(valueMap.get(LOCK_ATTR, String.class))) {
                valueMap.remove(LOCK_ATTR);
                valueMap.remove(LOCKED_SINCE_ATTR);
                try {
                    resolver.commit();
                } catch (PersistenceException e) {
                    throw new AppIntegrationException("Cannot rollback long-running write", e);
                }
            }
        }
    }

    private boolean canBeLocked(Resource resource) {
        final ValueMap valueMap = resource.getValueMap();
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
        final Calendar now = GregorianCalendar.getInstance();
        final Calendar lockedSince = valueMap.get(LOCKED_SINCE_ATTR, Calendar.class);
        if (lockedSince != null) {
            final long diff = now.getTimeInMillis() - lockedSince.getTimeInMillis();
            return diff >= MILLIS_UNTIL_LOCKS_EXPIRE;
        } else {
            return true;
        }
    }


    @Nonnull
    @Override
    public Supplier<InputStream> storeResource(@Nonnull ExternalResource resource) {

        try {
            // find existing entry
            final String cachePath = getCachePath(resource.getUri());
            final Resource cachePathRes = getOrCreateResource(cachePath);
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


            final Resource targetCacheRes = resolver.create(cachePathRes, entryName, null);
            final ModifiableValueMap modifiableValueMap = Objects.requireNonNull(targetCacheRes.adaptTo(ModifiableValueMap.class));
            modifiableValueMap.put(URI_ATTR, resource.getUri().toString());
            if (StringUtils.isNotBlank(versionId)) {
                modifiableValueMap.put(VERSION_ATTR, versionId);
            }

            final String fileName = StringUtils.substringAfterLast(cachePath, "/");
            final Resource res = resolver.create(targetCacheRes, fileName, Collections.singletonMap(JCR_PRIMARYTYPE, NT_FILE));

            Map<String, Object> propertiesMap = new HashMap<>();
            propertiesMap.put(JCR_PRIMARYTYPE, NT_RESOURCE);
            // ToDo: Get correct mime-type
            propertiesMap.put(JCR_MIMETYPE, "text/plain");
            propertiesMap.put(JCR_DATA, resource.getContentAsInputStream());
            resolver.create(res, JCR_CONTENT, propertiesMap);


            // ToDo: Re-check the panic commit's. This should follow a real plan
            resolver.commit();

            return () -> res.adaptTo(InputStream.class);

        } catch (IOException e) {
            throw new AppIntegrationException("Cannot store resource " + resource.getUri(), e);
        }
    }


    @Override
    @Nullable
    public ExternalResource getCachedResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory resourceFactory) {

        final Resource rootRes = resolver.getResource(rootPath);
        if (rootRes == null) {
            return null;
        }
        final String curVersion = StringUtils.defaultIfBlank(rootRes.getValueMap().get(VERSION_ATTR, String.class), null);

        // find existing entry
        final String cachePath = getCachePath(resourceRef.getUri());
        final Resource cachePathRes = resolver.getResource(cachePath);
        if (cachePathRes == null) {
            return null;
        }

        // iterate over all entry
        Iterator<Resource> cacheResIter = cachePathRes.listChildren();
        while (cacheResIter.hasNext()) {
            final Resource res = cacheResIter.next();
            final ValueMap valueMap = res.getValueMap();
            if (StringUtils.equals(resourceRef.getUri().toString(), valueMap.get(URI_ATTR, String.class))
                    && StringUtils.equals(curVersion, StringUtils.defaultIfBlank(valueMap.get(VERSION_ATTR, String.class), null))) {
                // found
                try {
                    final URI uri = new URI(Objects.requireNonNull(valueMap.get(URI_ATTR, String.class)));
                    final ExternalResourceType type = ExternalResourceType.parse(valueMap.get(TYPE_ATTR, String.class));

                    final String fileName = StringUtils.substringAfterLast(cachePath, "/");
                    final Resource dataRes = Objects.requireNonNull(res.getChild(fileName));

                    // ToDo: Implement futures for actual content
                    final InputStream content = Objects.requireNonNull(dataRes.adaptTo(InputStream.class));
                    final DataMap metadataMap = Optional.of(res)
                            .map(r -> r.getChild("metadata"))
                            .map(Resource::getValueMap)
                            .map(vm ->
                                    vm.entrySet().stream()
                                            .filter(entry -> !StringUtils.startsWith(entry.getKey(), "jcr:"))
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                            .map(DataMap::new)
                            .orElse(null);

                    return resourceFactory.createExternalResource(uri, type, content, metadataMap);

                } catch (URISyntaxException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        // nothing found in cache
        return null;
    }

    @Override
    public void markResourceRefreshed(@Nonnull ExternalResource resource) {
        // ToDo: add implementation
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


    private String getCachePath(URI uri) {
        final List<String> splitPath = new ArrayList<>(Arrays.asList(StringUtils.splitByWholeSeparator(uri.getPath(), "/")));
        if (StringUtils.isNotBlank(uri.getQuery())) {
            splitPath.add(Integer.toHexString(uri.getQuery().hashCode()));
        }
        for (int i = 0; i < splitPath.size(); i++) {
            splitPath.set(i, JcrUtil.escapeIllegalJcrChars(splitPath.get(i)));
        }
        return rootPath + JCR_PATH_SEPARATOR + String.join(JCR_PATH_SEPARATOR, splitPath);
    }
}
