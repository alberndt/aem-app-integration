package com.alexanderberndt.appintegration.engine.resources;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.*;

public class ExternalResourcesSet {

    private final Map<URI, ExternalResourceRef> knownReferencesMap = new HashMap<>();

    private final Deque<URI> unprocessedRefs = new ArrayDeque<>();

    public void add(@Nonnull ExternalResourceRef resourceRef) {
        final URI uri = resourceRef.getUri();
        final ExternalResourceRef prevResRef = knownReferencesMap.put(uri, resourceRef);
        if (prevResRef == null) {
            unprocessedRefs.addLast(uri);
        } else {
            // keep the expected resource type, if we already have more qualified information
            if (prevResRef.getExpectedType().isMoreQualifiedThan(resourceRef.getExpectedType())) {
                resourceRef.setExpectedType(prevResRef.getExpectedType());
            }
        }
    }

    public void addAll(Collection<ExternalResourceRef> referencedResources) {
        if (referencedResources != null) referencedResources.forEach(this::add);
    }

    public boolean hasMoreUnprocessed() {
        return !unprocessedRefs.isEmpty();
    }

    @Nonnull
    public ExternalResourceRef nextUnprocessed() {
        return Objects.requireNonNull(knownReferencesMap.get(unprocessedRefs.removeFirst()));
    }

    public int size() {
        return knownReferencesMap.size();
    }
}
