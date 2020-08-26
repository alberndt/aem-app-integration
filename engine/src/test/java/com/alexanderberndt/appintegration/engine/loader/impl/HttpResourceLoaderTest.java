package com.alexanderberndt.appintegration.engine.loader.impl;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.loader.HttpResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.conversion.StringConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

class HttpResourceLoaderTest {


    private ResourceLoader resourceLoader = new HttpResourceLoader();

    protected ExternalResource createExternalResource(InputStream inputStream, ExternalResourceRef resourceRef, ResourceLoader loader) {
        return new ExternalResource(loader, resourceRef, () -> Collections.singletonList(new StringConverter()));
    }


    @Test
    void load() throws IOException {
        ExternalResourceRef ref = new ExternalResourceRef("http://www.alexanderberndt.com", ExternalResourceType.HTML);

        ExternalResource resource = resourceLoader.load(ref, this::createExternalResource);
        System.out.println(resource.getContentAsParsedObject(String.class));
    }
}