package com.alexanderberndt.appintegration.engine.resources.loader;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import java.io.IOException;

public interface ResourceLoader {

    // ToDo: Handle Metadata

    ExternalResource load(String baseUrl, ExternalResourceRef resourceRef) throws IOException;

}
