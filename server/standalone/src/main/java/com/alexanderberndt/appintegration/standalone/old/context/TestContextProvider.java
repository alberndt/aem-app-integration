package com.alexanderberndt.appintegration.standalone.old.context;

import com.alexanderberndt.appintegration.engine.ApplicationInstance;
import com.alexanderberndt.appintegration.engine.ContextProvider;

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
