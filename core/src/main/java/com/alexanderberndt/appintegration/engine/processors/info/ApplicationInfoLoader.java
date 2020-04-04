package com.alexanderberndt.appintegration.engine.processors.info;

import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.api.ApplicationInfo;
import com.alexanderberndt.appintegration.engine.loader.ResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;

public class ApplicationInfoLoader {

    public ApplicationInfo load(ResourceLoader resourceLoader, String url) throws IOException {
        Reader reader = resourceLoader.load(url, Reader.class);
        if (reader == null) {
            throw new AppIntegrationException("Cannot locate application-info at " + url);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(reader, ApplicationInfoJson.class);
    }
}
