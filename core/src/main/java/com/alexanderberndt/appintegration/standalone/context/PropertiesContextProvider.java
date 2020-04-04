package com.alexanderberndt.appintegration.standalone.context;

import com.alexanderberndt.appintegration.api.ContextProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesContextProvider implements ContextProvider<String> {

    @Override
    public Map<String, String> getContext(String instance) {
        Map<String, String> context = new HashMap<>();

        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(instance);
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                for (String key : properties.stringPropertyNames()) {
                    context.put(key, properties.getProperty(key));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return context;
    }
}
