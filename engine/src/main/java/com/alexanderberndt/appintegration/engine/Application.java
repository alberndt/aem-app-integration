package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Data-record that defines an external application. It provides the location of the <code>application-info.json</code>
 * file and defines the used integration tools (loader, context providers, cache providers, ...).
 */
public interface Application {

    /**
     * Globally unique id for the application. This shall be the same id, as which is queried with
     * {@link AppIntegrationFactory#getApplication(String)}. Or it should be the same as the key of
     * {@link AppIntegrationFactory#getAllApplications()}.
     *
     * @return application-id
     */
    @Nonnull
    String getApplicationId();

    /**
     * Location of the <code>application-info.json</code> file. This url must be understood by used
     * resource-loader and cannot contain any placeholders.
     *
     * @return url of <code>application-info.json</code> file
     */
    @Nonnull
    String getApplicationInfoUrl();

    /**
     * Id of the resource-loader, where the predefined ones are <code>http</code>, <code>classloader</code>
     * and <code>file</code>. But additional resource-loaders could be available too.
     *
     * @return resource-loader id
     */
    @Nonnull
    String getResourceLoaderName();

    /**
     * List of context-providers - which must be available in the current environment.
     *
     * @return list of context-providers
     */
    @Nullable
    List<String> getContextProviderNames();

    @Nonnull
    String getProcessingPipelineName();

    @Nullable
    default Map<String, Object> getGlobalProperties() {
        return null;
    }

    enum FetchingMode {PREFETCH_ONLY, PREFETCH_AND_LIVE_LOAD, LIVE_LOAD_ONLY}

    @Nonnull
    default FetchingMode getFetchingMode() {
        return FetchingMode.PREFETCH_AND_LIVE_LOAD;
    }

}
