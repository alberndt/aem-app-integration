package com.alexanderberndt.appintegration.api;

import java.util.List;

/**
 * Data-record that defines an external application. It provides the location of the <code>application-info.json</code>
 * file and defines the used integration tools (loader, context providers, cache providers, ...).
 */
public interface Application {

    /**
     * Location of the <code>application-info.json</code> file. This url must be understood by used
     * resource-loader and cannot contain any placeholders.
     *
     * @return url of <code>application-info.json</code> file
     */
    String getApplicationInfoUrl();

    /**
     * Id of the resource-loader, where the predefined ones are <code>http</code>, <code>classloader</code>
     * and <code>file</code>. But additional resource-loaders could be available too.
     *
     * @return resource-loader id
     */
    String getUsedResourceLoader();

    /**
     * List of context-providers - which must be available in the current environment.
     *
     * @return list of context-providers
     */
    List<String> getUsedContextProviders();

}
