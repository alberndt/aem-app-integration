package com.alexanderberndt.appintegration.engine.resources.loader;

import javax.annotation.Nullable;

public interface ResourceLoaderFactory {

    @Nullable
    ResourceLoader getResourceLoader(String name);

}
