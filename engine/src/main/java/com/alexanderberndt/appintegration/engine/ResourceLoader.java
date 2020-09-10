package com.alexanderberndt.appintegration.engine;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;

public interface ResourceLoader {

    @Nonnull
    ExternalResource load(@Nonnull final ExternalResourceRef resourceRef, @Nonnull final ExternalResourceFactory factory) throws IOException, ResourceLoaderException;

    @Nullable
    default URI getDefaultBaseUri() {
        return null;
    }

}
