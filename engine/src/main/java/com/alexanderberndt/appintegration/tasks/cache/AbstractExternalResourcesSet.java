package com.alexanderberndt.appintegration.tasks.cache;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourcesSet;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractExternalResourcesSet implements ExternalResourcesSet {

    private final Map<URI, ExternalResourceRef> knownReferencesMap = new LinkedHashMap<>();

    @Override
    public final void addResourceReference(@Nonnull ExternalResourceRef resourceRef) {
        ExternalResourceRef prevResRef = knownReferencesMap.put(resourceRef.getUri(), resourceRef);
        // keep the expected resource type, if we already have more qualified information
        if ((prevResRef != null) && (prevResRef.getExpectedType() != null)
                && prevResRef.getExpectedType().isMoreQualifiedThan(resourceRef.getExpectedType())) {
            resourceRef.setExpectedType(prevResRef.getExpectedType());
        }
    }


    // ToDo: Allow async pre-fetching, globally limited thread pool

    // ToDo: Support A/B Switch

    // ToDo: Support Persistent Cache

    // ToDo: Support Cache Headers

}
