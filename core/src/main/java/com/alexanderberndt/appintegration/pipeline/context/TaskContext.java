package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.configuration.ConfigurationException;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Set;

public class TaskContext {

    public static final String NAMESPACE_SEPARATOR = ":";

    public static final String RESOURCE_TYPE_SEPARATOR = ".";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GlobalContext globalContext;

    private final Ranking rank;

    private final String taskNamespace;

    private final ExternalResourceType resourceType;


    protected TaskContext(GlobalContext globalContext, Ranking rank, String taskNamespace, ExternalResourceType resourceType) {
        this.globalContext = globalContext;
        this.rank = rank;
        this.taskNamespace = taskNamespace;
        this.resourceType = resourceType;
    }

    public void addWarning(String message) {
        globalContext.addWarning(taskNamespace + ": " + message);
    }

    public void addError(String message) {
        globalContext.addError(taskNamespace + ": " + message);
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return globalContext.getResourceLoader();
    }

    @SuppressWarnings("unused")
    public Ranking getRank() {
        return rank;
    }

    public Object getValue(@Nonnull String key) {
        final NamespaceKey nk = parseNamespaceKey(key);
        return globalContext.getProcessingParams().getValue(nk.namespace, nk.key, resourceType);
    }

    public <T> T getValue(@Nonnull String key, @Nonnull Class<T> type) {
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            return globalContext.getProcessingParams().getValue(nk.namespace, nk.key, resourceType, type);
        } catch (ConfigurationException e) {
            addWarning(e.getMessage());
            return null;
        }
    }

    @Nonnull
    public <T> T getValue(@Nonnull String key, @Nonnull T defaultValue) {
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            return globalContext.getProcessingParams().getValue(nk.namespace, nk.key, resourceType, defaultValue);
        } catch (ConfigurationException e) {
            addWarning(e.getMessage());
            return defaultValue;
        }
    }

    public void setValue(@Nonnull String key, @Nullable Object value) {
        LOG.debug("setValue({}, {}, {}) @ {}", rank, key, value, this.taskNamespace);
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            globalContext.getProcessingParams().setValue(nk.namespace, nk.key, rank, resourceType, value);
        } catch (ConfigurationException e) {
            addWarning(e.getMessage());
        }
    }

    public Class<?> getType(@Nonnull String key) {
        final NamespaceKey nk = parseNamespaceKey(key);
        return globalContext.getProcessingParams().getType(nk.namespace, nk.key);
    }

    public void setType(@Nonnull String key, Class<?> type) {
        LOG.debug("setType({}, {}, {}) @ {}", rank, key, type, this.taskNamespace);
        final NamespaceKey nk = parseNamespaceKey(key);
        try {
            globalContext.getProcessingParams().setType(nk.namespace, nk.key, type);
        } catch (ConfigurationException e) {
            addError(e.getMessage());
        }
    }

    public Set<String> keySet() {
        return globalContext.getProcessingParams().keySet(taskNamespace);
    }

    public void setKeyComplete() {
        LOG.debug("setKeyComplete({}) @ {} with keys = {}", rank, this.taskNamespace, this.keySet());
        globalContext.getProcessingParams().setKeyComplete(taskNamespace);
    }

    protected NamespaceKey parseNamespaceKey(@Nonnull String key) {
        final int nameSpaceSeparatorIndex = key.indexOf(NAMESPACE_SEPARATOR);
        final String namespace;
        if (nameSpaceSeparatorIndex > 0) {
            namespace = key.substring(nameSpaceSeparatorIndex + NAMESPACE_SEPARATOR.length());
            key = key.substring(0, nameSpaceSeparatorIndex);
        } else {
            namespace = this.taskNamespace;
        }

        final int resourceTypeSeparatorIndex = key.lastIndexOf(RESOURCE_TYPE_SEPARATOR);
        final ExternalResourceType resourceTypeFromKey;
        if (resourceTypeSeparatorIndex > 0) {
            final ExternalResourceType parsedResourceTypeFromKey =
                    ExternalResourceType.parse(key.substring(resourceTypeSeparatorIndex + RESOURCE_TYPE_SEPARATOR.length()));
            if (parsedResourceTypeFromKey != null) {
                resourceTypeFromKey = parsedResourceTypeFromKey;
                key = key.substring(0, resourceTypeSeparatorIndex);
            } else {
                resourceTypeFromKey = ExternalResourceType.ANY;
            }
        } else {
            resourceTypeFromKey = ExternalResourceType.ANY;
        }

        return new NamespaceKey(namespace, key, resourceTypeFromKey);
    }

    protected static class NamespaceKey {

        private final String namespace;
        private final String key;
        private final ExternalResourceType resourceType;

        public NamespaceKey(String namespace, String key, ExternalResourceType resourceType) {
            this.namespace = namespace;
            this.key = key;
            this.resourceType = resourceType;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getKey() {
            return key;
        }

        public ExternalResourceType getResourceType() {
            return resourceType;
        }
    }
}
