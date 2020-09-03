package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.Application;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CoreTestApplication implements Application {

    @Nonnull
    private final String applicationInfoUrl;

    @Nonnull
    private final String resourceLoaderName;

    @Nonnull
    private final String processingPipelineName;

    @Nullable
    private final List<String> contextProviderNames;

    @Nullable
    private final Map<String, Object> globalProperties;

    public CoreTestApplication(@Nonnull String applicationInfoUrl,
                               @Nonnull String resourceLoaderName,
                               @Nonnull String processingPipelineName,
                               @Nullable List<String> contextProviderNames,
                               @Nullable Map<String, Object> globalProperties) {
        this.applicationInfoUrl = applicationInfoUrl;
        this.resourceLoaderName = resourceLoaderName;
        this.processingPipelineName = processingPipelineName;
        this.contextProviderNames = contextProviderNames;
        this.globalProperties = globalProperties;
    }

    @Override
    @Nonnull
    public String getApplicationInfoUrl() {
        return applicationInfoUrl;
    }

    @Override
    @Nonnull
    public String getResourceLoaderName() {
        return resourceLoaderName;
    }

    @Override
    @Nonnull
    public String getProcessingPipelineName() {
        return processingPipelineName;
    }

    @Override
    @Nullable
    public List<String> getContextProviderNames() {
        return contextProviderNames;
    }

    @Override
    @Nullable
    public Map<String, Object> getGlobalProperties() {
        return globalProperties;
    }
}
