package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestResourceLoader implements ResourceLoader {

    private String testContent;

    public TestResourceLoader(String testContent) {
        this.testContent = testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    @Nonnull
    @Override
    public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) throws IOException, ResourceLoaderException {
        return factory.createExternalResource(resourceRef, new ByteArrayInputStream(testContent.getBytes()));
    }


}
