package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provides a key-value map for an specific application instance. As example it could provide country and language.
 * These variables can then be passed to the application, as example as part of the URL to the html-snippet
 * (e.g. <code>/subscribe.html?lang=${lang}</code>). This allows the application to render context-dependent.
 * <p>
 * <em>Hint</em>: Review some default context-providers, before you implement your own ones.
 *
 * @param <I> Environment-dependent instance-type, e.g. a Sling-Resource in case of Apache-Sling App-Integration.
 */
public interface ContextProvider<I extends ApplicationInstance> {

    /**
     * Calculates a key-value map, with variables and instance-specific values.
     *
     * @param instance Application instance
     * @return context map
     */
    @Nullable
    Map<String, String> getContext(I instance);

}
