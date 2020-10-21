package com.alexanderberndt.appintegration.engine.cache;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;

public interface ExternalResourceCache {

    @Nonnull
    ExternalResource storeResource(@Nonnull ExternalResource resource);

    @Nullable
    ExternalResource getCachedResource(@Nonnull URI uri, @Nonnull ExternalResourceFactory resourceFactory);

    boolean isLongRunningWrite();

    void startLongRunningWrite(@Nullable String nameHint);

    void commitLongRunningWrite();

    void rollbackLongRunningWrite();


}
