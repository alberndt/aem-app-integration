package com.alexanderberndt.appintegration.engine.context;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.configuration.ConfigurationException;
import com.alexanderberndt.appintegration.pipeline.configuration.MultiValue;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private final GlobalContext<?,?> globalContext;

    @Nonnull
    private final Ranking rank;

    @Nonnull
    private final String taskNamespace;

    @Nonnull
    private final ExternalResourceType resourceType;

    @Nullable
    private final DataMap executionDataMap;

    @Nonnull
    private final TaskLogger taskLogger;

    private final AbstractLogger readConfigurationLog;


    protected TaskContext(
            @Nonnull GlobalContext<?,?> globalContext,
            @Nonnull TaskLogger taskLogger,
            @Nonnull Ranking rank,
            @Nonnull String taskNamespace,
            @Nonnull ExternalResourceType resourceType,
            @Nullable DataMap executionDataMap) {
        this.globalContext = Objects.requireNonNull(globalContext, "GlobalContext MUST NOT NULL!");
        this.taskLogger = Objects.requireNonNull(taskLogger, "TaskLogger MUST NOT NULL!");
        this.readConfigurationLog = taskLogger.createDetailsLogger("Effective configuration...");
        this.rank = Objects.requireNonNull(rank, "Ranking MUST NOT NULL!");
        this.taskNamespace = Objects.requireNonNull(taskNamespace, "TaskNamespace MUST NOT NULL!");
        this.resourceType = Objects.requireNonNull(resourceType, "ExternalResourceType MUST NOT NULL!");
        this.executionDataMap = executionDataMap;
    }

    public void addWarning(@Nonnull String message, Object... args) {
        taskLogger.addWarning(taskNamespace + ": " + message, args);
    }

    public void addError(@Nonnull String message, Object... args) {
        taskLogger.addError(taskNamespace + ": " + message, args);
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        return globalContext.getResourceLoader();
    }

    @Nonnull
    public ExternalResourceFactory getResourceFactory() {
        return globalContext.getResourceFactory();
    }

    @Nonnull
    public ExternalResourceCache getExternalResourceCache() {
        return globalContext.getExternalResourceCache();
    }

    public String getApplicationId() {
        return globalContext.getApplicationId();
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
            readConfigurationLog.addWarning("%s = %s (default, wrong type)", nk.getPlainKey(), defaultValue);
            return defaultValue;
        }

        final T value = this.getValue(key, (Class<T>) defaultValue.getClass());
        return (value != null) ? value : defaultValue;
    }

    protected Object getValueInternal(NamespaceKey nk) {
        Object returnValue;
        final Object executionValue = (executionDataMap != null) ? executionDataMap.get(nk.getPlainKey()) : null;
        if (executionValue != null) {
            try {
                returnValue = globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType, executionValue);
            } catch (ConfigurationException e) {
                addWarning(e.getMessage());
                returnValue = globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType);
            }
        } else {
            returnValue = globalContext.getProcessingParams().getValue(nk.getNamespace(), nk.getKey(), resourceType);
        }
        readConfigurationLog.addInfo("%s = %s", nk.getPlainKey(), returnValue);
        return returnValue;
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
                    if (executionDataMap != null) {
                        executionDataMap.put(nk.getPlainKey(), value);
                    } else {
                        addWarning("Can't set %s=%s, as no execution-data-map was provided",nk.getPlainKey(), value);
                    }
                } else {
                    addWarning("Value %s is invalid type for %s", value, nk.getPlainKey());
                }
            } else {
                globalContext.getProcessingParams().setValue(nk.getNamespace(), nk.getKey(), rank, nk.getResourceType(), value);
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

    public DataMap getConfiguration()  {

        final DataMap configuration = new DataMap();

        final Map<String, MultiValue> configurationValues = globalContext.getProcessingParams().configurationValues(taskNamespace);
        for (Map.Entry<String, MultiValue> entry : configurationValues.entrySet()) {
            Object value;
            try {
                final Object execValue = (executionDataMap != null) ? executionDataMap.get(entry.getKey()) : null;
                if (execValue != null) {
                    value = entry.getValue().getValue(resourceType, execValue);
                } else {
                    value = entry.getValue().getValue(resourceType);
                }
            } catch (ConfigurationException e) {
                value = entry.getValue().getValue(resourceType);
            }
            configuration.setData(entry.getKey(), value);
        }

        return configuration;
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
                    addWarning(typeSpecifierWarning, args);
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
