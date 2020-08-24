package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nullable;

public interface ResourceLoaderFactory {

    @Nullable
    ResourceLoader getResourceLoader(String name);

}
