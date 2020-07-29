package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpResourceLoaderTest {


    private ResourceLoader resourceLoader = new HttpResourceLoader();

    @Test
    void load() throws IOException {
        ExternalResourceRef ref = new ExternalResourceRef("http://www.alexanderberndt.com", ExternalResourceType.HTML);

        ExternalResource resource = resourceLoader.load(null, ref);
        System.out.println(resource.getString());
    }
}