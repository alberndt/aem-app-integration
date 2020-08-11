package com.alexanderberndt.appintegration.tasks.cache;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourcesSet;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractExternalResourcesSet implements ExternalResourcesSet {

    private final Map<String, ExternalResourceRef> knownReferencesMap = new LinkedHashMap<>();

    private final Set<String> prefetchedResources = new HashSet<>();

    private boolean isPrefetchAll = false;

    private final Set<ExternalResourceType> prefetchTypesSet = new HashSet<>();

    protected abstract void prefetch(ExternalResourceRef resourceRef);

    protected abstract ExternalResource getResource(ExternalResourceRef resourceRef, boolean isPrefetched);

    @Override
    public final void addResourceReference(@Nonnull ExternalResourceRef resourceRef) {
        final String relativeUrl = resourceRef.getUrl();
        if (StringUtils.isNotBlank(relativeUrl)) {
            ExternalResourceRef prevResRef = knownReferencesMap.put(resourceRef.getUrl(), resourceRef);
            // keep the expected resource type, if we already have more qualified information
            if ((prevResRef != null) && (prevResRef.getExpectedType() != null)
                    && prevResRef.getExpectedType().isMoreQualified(resourceRef.getExpectedType())) {
                resourceRef.setExpectedType(prevResRef.getExpectedType());
            }

            prefetchInternal(resourceRef, ref -> isPrefetchAll || prefetchTypesSet.contains(resourceRef.getExpectedType()));
        }
    }

    @Override
    public final ExternalResource getResource(String relativeUrl) {
        ExternalResourceRef resourceRef = knownReferencesMap.get(relativeUrl);
        if (resourceRef != null) {
            return getResource(resourceRef, prefetchedResources.contains(relativeUrl));
        } else {
            // ToDo: Throw Unknown Resource Exception
            return null;
        }
    }

    @Override
    public final void prefetch(ExternalResourceType... prefetchTypes) {
        if (prefetchTypes != null) {
            final Set<ExternalResourceType> prefetchTypesSet = new HashSet<>(Arrays.asList(prefetchTypes));
            prefetchInternal(ref -> prefetchTypesSet.contains(ref.getExpectedType()));
        }
    }

    @Override
    public final void prefetchAll() {
        prefetchInternal(ref -> true);
        isPrefetchAll = true;
    }

    private void prefetchInternal(@Nonnull Predicate<ExternalResourceRef> predicate) {
        knownReferencesMap.values().forEach(ref -> prefetchInternal(ref, predicate));
    }


    private void prefetchInternal(@Nonnull ExternalResourceRef resourceRef, @Nonnull Predicate<ExternalResourceRef> predicate) {
        if (!prefetchedResources.contains(resourceRef.getUrl()) && predicate.test(resourceRef)) {
            prefetch(resourceRef);
            prefetchedResources.add(resourceRef.getUrl());
        }
    }

    // ToDo: Allow async pre-fetching, globally limited thread pool

    // ToDo: Support A/B Switch

    // ToDo: Support Persistent Cache

    // ToDo: Support Cache Headers

}
