package com.alexanderberndt.appintegration.standalone.old.context;

import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.ApplicationInstance;

import java.util.HashMap;
import java.util.Map;

public class TestContextProvider implements ContextProvider<ApplicationInstance> {

    @Override
    public Map<String, String> getContext(ApplicationInstance instance) {
        Map<String, String> context = new HashMap<>();
        context.put("hello", "world");
        return context;
    }
}
