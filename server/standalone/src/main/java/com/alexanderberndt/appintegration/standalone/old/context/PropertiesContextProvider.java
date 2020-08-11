package com.alexanderberndt.appintegration.standalone.old.context;

import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.ApplicationInstance;

import javax.annotation.Nullable;
import java.util.Map;

public class PropertiesContextProvider implements ContextProvider<ApplicationInstance> {

    @Nullable
    @Override
    public Map<String, String> getContext(ApplicationInstance instance) {
        throw new UnsupportedOperationException("method not implemented!");

//        Map<String, String> context = new HashMap<>();
//
//
//        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(instance);
//        if (inputStream != null) {
//            Properties properties = new Properties();
//            try {
//                properties.load(inputStream);
//                for (String key : properties.stringPropertyNames()) {
//                    context.put(key, properties.getProperty(key));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return context;
    }
}
