package com.alexanderberndt.appintegration.api;

import java.util.Map;
import java.util.stream.Collectors;

public interface ApplicationInfo {

    String getName();

    String getVersion();

    Map<String, ComponentInfo> getComponents();

    default String debugString() {
        final String componentsDebugString;
        if (getComponents() == null) {
            componentsDebugString = "null";
        } else {
            componentsDebugString = "[" + getComponents().entrySet().stream()
                    .map(entry -> "'" + entry.getKey() + "' -> " + entry.getValue().debugString())
                    .collect(Collectors.joining(", ")) + "]";
        }

        return "ApplicationInfo{" +
                "name='" + getName() + '\'' +
                ", version='" + getVersion() + '\'' +
                ", components=" + componentsDebugString +
                '}';
    }


    interface ComponentInfo {

        String getName();

        String getUrl();

        String getDialog();

        default String debugString() {
            return "Component{" +
                    "name='" + getName() + '\'' +
                    ", url='" + getUrl() + '\'' +
                    ", dialog='" + getDialog() + "'}";
        }
    }
}
