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
import java.util.*;

/**
 * The TaskContext is a light-weight facade for the {@link GlobalContext}, which adds the specific processing-context
 * for the called task. This object is created everytime when any task-method is called.
 *
 * @see com.alexanderberndt.appintegration.pipeline.task.PreparationTask
 * @see com.alexanderberndt.appintegration.pipeline.task.LoadingTask
 * @see com.alexanderberndt.appintegration.pipeline.task.ProcessingTask
 */
public class TaskContext {

    public static final String NAMESPACE_SEPARATOR = ":";

    public static final String RESOURCE_TYPE_SEPARATOR = ".";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final GlobalContext globalContext;

    @Nonnull
    private final Ranking rank;

    @Nonnull
    private final String taskNamespace;

    @Nonnull
    private final ExternalResourceType resourceType;

    @Nonnull
    private final Map<String, Object> executionDataMap;


    protected TaskContext(
            @Nonnull GlobalContext globalContext,
            @Nonnull Ranking rank,
            @Nonnull String taskNamespace,
            @Nonnull ExternalResourceType resourceType,
            @Nonnull Map<String, Object> executionDataMap) {
        this.globalContext = globalContext;
        this.rank = rank;
        this.taskNamespace = taskNamespace;
        this.resourceType = resourceType;
        this.executionDataMap = executionDataMap;
    }

    public void addWarning(@Nonnull String message, Object... args) {
        globalContext.addWarning(taskNamespace + ": " + formatMessage(message, args));
    }

    public void addError(@Nonnull String message, Object... args) {
        globalContext.addError(taskNamespace + ": " + formatMessage(message, args));
    }

    protected String formatMessage(String message, Object... args) {
        String formattedMsg;
        try {
            formattedMsg = String.format(message, args);
        } catch (IllegalFormatException e) {
            formattedMsg = message + " " + Arrays.toString(args);
        }
        return formattedMsg;
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return globalContext.getResourceLoader();
    }

    public Object getValue(@Nonnull String key) {
        final NamespaceKey nk = parseNamespaceKey(key, false,
                "The key %s for getValue() SHOULD NOT have any type-specifier. It will be ignored!", key);
        return getValueInternal(nk);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nonnull String key, @Nonnull Class<T> expectedType) {
        final Object value = this.getValue(key);
        if (value == null) {
            return null;
        } else {
            Objects.requireNonNull(expectedType, "Parameter expectedType MUST NOT null!");
            if (expectedType.isInstance(value)) {
                return (T) value;
            } else {
                addWarning(String.format("parameter %s is requested as %s, but is %s!", key, expectedType, value.getClass()));
                return null;
            }
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nonnull String key, @Nonnull T defaultValue) {

        final NamespaceKey nk = parseNamespaceKey(key, false,
                "The key %s for getValue() SHOULD NOT have any type-specifier. It will be ignored!", key);

        if (!globalContext.getProcessingParams().isValidType(nk.getNamespace(), nk.getKey(), defaultValue)) {
            addWarning("Type of default-value %s (%s) is not valid for key %s!", defaultValue, defaultValue.getClass(), key);
            return defaultValue;
        }

        final T value = this.getValue(key, (Class<T>) defaultValue.getClass());
        return (value != null) ? value : defaultValue;
    }

    protected Object getValueInternal(NamespaceKey nk) {
        final Object executionValue = executionDataMap.get(nk.getPlainKey());
        if (executionValue != null) {
            try {
                return globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType, executionValue);
            } catch (ConfigurationException e) {
                addWarning(e.getMessage());
                return globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType);
            }
        } else {
            return globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType);
        }
    }

    // ToDo: Check for required namespace during execution
    public void setValue(@Nonnull String key, @Nullable Object value) {
        LOG.debug("setValue({}, {}, {}) @ {}", rank, key, value, this.taskNamespace);
        try {
            final NamespaceKey nk = parseNamespaceKey(key, rank != Ranking.PIPELINE_EXECUTION,
                    "The key %s for setValue() can have type-specifier only for configuration. "
                            + " It will be ignored during pipeline-execution!", key);

            if (rank == Ranking.PIPELINE_EXECUTION) {
                if (globalContext.getProcessingParams().isValidType(nk.getNamespace(), nk.getKey(), value)) {
                    executionDataMap.put(nk.getPlainKey(), value);
                } else {
                    addWarning(String.format("Value %s is invalid type for %s", value, nk.getPlainKey()));
                }
            } else {
                globalContext.getProcessingParams().setValue(nk.getNamespace(), nk.getKey(), rank, resourceType, value);
            }
        } catch (ConfigurationException e) {
            addWarning(e.getMessage());
        }
    }

    public Class<?> getType(@Nonnull String key) {
        final NamespaceKey nk = parseNamespaceKey(key, false,
                "The key %s for getType() SHOULD NOT have any type-specifier. It will be ignored!", key);
        return globalContext.getProcessingParams().getType(nk.getNamespace(), nk.getKey());
    }

    public void setType(@Nonnull String key, Class<?> type) {
        LOG.debug("setType({}, {}, {}) @ {}", rank, key, type, this.taskNamespace);
        final NamespaceKey nk = parseNamespaceKey(key, false,
                "The key %s for setType() SHOULD NOT have any type-specifier. It will be ignored!", key);
        try {
            globalContext.getProcessingParams().setType(nk.getNamespace(), nk.getKey(), type);
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

    protected NamespaceKey parseNamespaceKey(@Nonnull final String key, boolean isTypeSpecifierAllowed,
                                             String typeSpecifierWarning, Object... args) {
        final int nameSpaceSeparatorIndex = key.indexOf(NAMESPACE_SEPARATOR);
        final String namespace;
        final String keyWithoutNamespace;
        if (nameSpaceSeparatorIndex > 0) {
            namespace = key.substring(0, nameSpaceSeparatorIndex);
            keyWithoutNamespace = key.substring(nameSpaceSeparatorIndex + NAMESPACE_SEPARATOR.length());
        } else {
            namespace = this.taskNamespace;
            keyWithoutNamespace = key;
        }

        final int resourceTypeSeparatorIndex = keyWithoutNamespace.lastIndexOf(RESOURCE_TYPE_SEPARATOR);
        ExternalResourceType resourceTypeFromKey = ExternalResourceType.ANY;
        String pureKey = keyWithoutNamespace;
        if (resourceTypeSeparatorIndex > 0) {
            final ExternalResourceType parsedResourceTypeFromKey =
                    ExternalResourceType.parse(keyWithoutNamespace.substring(resourceTypeSeparatorIndex + RESOURCE_TYPE_SEPARATOR.length()));
            if (parsedResourceTypeFromKey != null) {
                pureKey = keyWithoutNamespace.substring(0, resourceTypeSeparatorIndex);
                if (isTypeSpecifierAllowed) {
                    resourceTypeFromKey = parsedResourceTypeFromKey;
                } else {
                    this.addWarning(typeSpecifierWarning, args);
                }
            }
        }

        return new NamespaceKey(namespace, pureKey, resourceTypeFromKey);
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

        public String getPlainKey() {
            return namespace + NAMESPACE_SEPARATOR + key;
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
