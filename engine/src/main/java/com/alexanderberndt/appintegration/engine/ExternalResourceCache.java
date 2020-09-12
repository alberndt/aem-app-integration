package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ExternalResourceCache {

    void storeResource(@Nonnull ExternalResource resource);

    void markResourceRefreshed(@Nonnull ExternalResource resource);

    @Nullable
    ExternalResource getCachedResource(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory resourceFactory);

    boolean startLongRunningWrite(@Nullable String nameHint);

    void continueLongRunningWrite();

    void commitLongRunningWrite();

    void rollbackLongRunningWrite();

}
