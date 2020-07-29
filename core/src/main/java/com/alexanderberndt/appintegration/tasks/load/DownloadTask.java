package com.alexanderberndt.appintegration.tasks.load;

import com.alexanderberndt.appintegration.api.task.LoadingTask;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.TaskContext;

import java.io.IOException;

public class DownloadTask implements LoadingTask {

    @Override
    public ExternalResource load(TaskContext context, ExternalResourceRef resourceRef) {
        String loaderName = context.getTaskParams().require("loader", String.class);
        ResourceLoader resourceLoader = context.getResourceLoaderFactory().getResourceLoader(loaderName);

        if (resourceLoader == null) {
            throw new AppIntegrationException("Cannot get loader " + loaderName + " from ResourceLoaderFactory");
        }

        try {
            return resourceLoader.load(null, resourceRef);
        } catch (IOException e) {
            throw new AppIntegrationException("Failed to load resource " + resourceRef.getRelativeUrl(), e);
        }
    }
}
