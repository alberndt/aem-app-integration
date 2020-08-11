package com.alexanderberndt.appintegration.tasks.process.info;

import com.alexanderberndt.appintegration.api.ApplicationInfo;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;

@Deprecated
public class ApplicationInfoLoader {

    public ApplicationInfo load(ResourceLoader resourceLoader, String url) throws IOException {
        Reader reader = null; //resourceLoader.load(url,null /* Reader.class */);
        if (reader == null) {
            throw new AppIntegrationException("Cannot locate application-info at " + url);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(reader, ApplicationInfoJson.class);
    }
}
