package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.ResourceLoader;

import java.io.InputStream;

public interface ExternalResourceFactory {

    ExternalResource createExternalResource(InputStream inputStream, ExternalResourceRef resourceRef, ResourceLoader loader);
}
