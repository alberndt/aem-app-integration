package com.alexanderberndt.appintegration.standalone.old.context;

import com.alexanderberndt.appintegration.api.ContextProvider;

import java.util.HashMap;
import java.util.Map;

public class TestContextProvider implements ContextProvider<String> {

    @Override
    public Map<String, String> getContext(String instance) {
        Map<String, String> context = new HashMap<>();
        context.put("hello", "world");
        return context;
    }
}
