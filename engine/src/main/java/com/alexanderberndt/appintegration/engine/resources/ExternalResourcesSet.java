package com.alexanderberndt.appintegration.engine.resources;

public interface ExternalResourcesSet {


    default void addResourceReference(String relativeUrl, ExternalResourceType expectedType) {
        addResourceReference(new ExternalResourceRef(relativeUrl, expectedType));
    }

    void addResourceReference(ExternalResourceRef resourceRef);


}
