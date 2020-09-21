package com.alexanderberndt.appintegration.engine.resources;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExternalResourcesSet extends AbstractSet<ExternalResourceRef> {

    private final Map<URI, ExternalResourceRef> knownReferencesMap = new LinkedHashMap<>();

    @Nonnull
    @Override
    public Iterator<ExternalResourceRef> iterator() {
        return knownReferencesMap.values().iterator();
    }

    @Override
    public int size() {
        return knownReferencesMap.size();
    }

    @Override
    public boolean add(ExternalResourceRef resourceRef) {
        ExternalResourceRef prevResRef = knownReferencesMap.put(resourceRef.getUri(), resourceRef);
        // keep the expected resource type, if we already have more qualified information
        if ((prevResRef != null) && prevResRef.getExpectedType().isMoreQualifiedThan(resourceRef.getExpectedType())) {
            resourceRef.setExpectedType(prevResRef.getExpectedType());
        }
        return prevResRef == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExternalResourcesSet that = (ExternalResourcesSet) o;
        return knownReferencesMap.keySet().equals(that.knownReferencesMap.keySet());
    }

    @Override
    public int hashCode() {
        return knownReferencesMap.keySet().hashCode();
    }

}
