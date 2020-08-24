package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Data-record that defines an external application. It provides the location of the <code>application-info.json</code>
 * file and defines the used integration tools (loader, context providers, cache providers, ...).
 */
public class Application {

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

    public Application(@Nonnull String applicationInfoUrl,
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

    /**
     * Location of the <code>application-info.json</code> file. This url must be understood by used
     * resource-loader and cannot contain any placeholders.
     *
     * @return url of <code>application-info.json</code> file
     */
    @Nonnull
    public String getApplicationInfoUrl() {
        return applicationInfoUrl;
    }

    /**
     * Id of the resource-loader, where the predefined ones are <code>http</code>, <code>classloader</code>
     * and <code>file</code>. But additional resource-loaders could be available too.
     *
     * @return resource-loader id
     */
    @Nonnull
    public String getResourceLoaderName() {
        return resourceLoaderName;
    }

    /**
     * List of context-providers - which must be available in the current environment.
     *
     * @return list of context-providers
     */
    @Nullable
    public List<String> getContextProviderNames() {
        return contextProviderNames;
    }

    @Nonnull
    public String getProcessingPipelineName() {
        return processingPipelineName;
    }

    @Nullable
    public Map<String, Object> getGlobalProperties() {
        return globalProperties;
    }
}
