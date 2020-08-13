package com.alexanderberndt.appintegration.pipeline.configuration;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PipelineConfiguration {

    private static final String INTERNAL_NAMESPACE_SEPARATOR = "#°#";

    private final Map<String, MultiValue> values = new HashMap<>();

    private final Set<String> keyCompleteNamespaces = new HashSet<>();

    private boolean isReadOnly = false;

    public Object getValue(@Nullable String namespace, @Nonnull String key, @Nonnull ExternalResourceType resourceType) {
        return getEntryAndMap(namespace, key, multiValue -> multiValue.getValue(resourceType));
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nullable String namespace, @Nonnull String key, @Nonnull ExternalResourceType resourceType, @Nonnull Class<T> type) throws ConfigurationException {
        final MultiValue multiValue = getEntryAndMap(namespace, key, Function.identity());
        if (multiValue == null) {
            return null;
        } else {
            final Class<?> expectedType = multiValue.getType();
            if ((expectedType == null) || (expectedType == type)) {
                return (T) multiValue.getValue(resourceType);
            } else {
                throw new ConfigurationException(String.format("parameter %s is requested as %s, but is %s!",
                        key, type.getSimpleName(), multiValue.getType().getSimpleName()));
            }
        }
    }

    @Nonnull
    public Object requireValue(@Nullable String namespace, String key, @Nonnull ExternalResourceType resourceType, Class<?> type) throws ConfigurationException {
        final Object value = getValue(namespace, key, resourceType, type);
        if (value != null) {
            return value;
        } else {
            throw new ConfigurationException(String.format("missing required parameter %s!", key));
        }
    }

    @Nonnull
    public <T> T getValue(@Nullable String namespace, @Nonnull String key, @Nonnull ExternalResourceType resourceType, @Nonnull final T executionValue) throws ConfigurationException {
        final MultiValue multiValue = getEntryAndMap(namespace, key, Function.identity());
        if (multiValue == null) {
            return executionValue;
        } else {
            return multiValue.getValue(resourceType, executionValue);
        }
    }

    public Class<?> getType(@Nullable String namespace, @Nonnull String key) {
        return getEntryAndMap(namespace, key, MultiValue::getType);
    }

    public String getTypeName(@Nullable String namespace, @Nonnull String key) {
        return getEntryAndMap(namespace, key, MultiValue::getTypeName);
    }

    public Set<String> namespaceSet() {
        return values.keySet().stream()
                .map(key -> StringUtils.substringBefore(key, INTERNAL_NAMESPACE_SEPARATOR))
                .collect(Collectors.toSet());
    }

    public Set<String> keySet(String namespace) {
        final String prefix = getNamespaceId(namespace) + INTERNAL_NAMESPACE_SEPARATOR;
        return values.keySet().stream()
                .filter(key -> StringUtils.startsWith(key, prefix))
                .map(key -> StringUtils.removeStart(key, prefix))
                .collect(Collectors.toSet());
    }

    /**
     * Sets a value, if certain conditions are fulfilled. Otherwise an exception is thrown.
     *
     * @param namespace Namespace of the entry. If null, it is assumed the "global" namespace.
     * @param key       Key of the entry
     * @param rank      Ranking of the setter. An exception is thrown, if the entry (namespace/key) was already set by
     *                  a more dominant rank.
     * @param value     Value for the entry. The type of the value must be the same, as any previous set value or valueType.
     *                  If the type is a Number or Boolean, then the value must-not be null. Otherwise an exception is thrown.
     * @throws ConfigurationException Thrown, if value was not set
     */
    public void setValue(@Nullable String namespace, @Nonnull String key, @Nonnull Ranking rank, @Nonnull ExternalResourceType resourceType, @Nullable Object value) throws ConfigurationException {

        if (isReadOnly) throw new ConfigurationException("Configuration is read-only. Cannot set value!");

        final String internalKey = getInternalKey(namespace, key);
        final MultiValue existingRecord = values.get(internalKey);
        if (existingRecord != null) {
            existingRecord.setValue(rank, resourceType, value);
        } else {
            if (!keyCompleteNamespaces.contains(namespace)) {
                values.put(internalKey, MultiValue.createByValue(rank, resourceType, value));
            } else {
                throw new ConfigurationException(String.format("Variable %s is not allowed (namespace %s is key-complete)!",
                        internalKey, getNamespaceId(namespace)));
            }
        }
    }

    public void setType(@Nullable String namespace, @Nonnull String key, @Nonnull Class<?> type) throws ConfigurationException {

        if (isReadOnly) throw new ConfigurationException("Configuration is read-only. Cannot set type!");

        final String internalKey = getInternalKey(namespace, key);
        final MultiValue existingRecord = values.get(internalKey);
        if (existingRecord != null) {
            existingRecord.setType(type);
        } else {
            values.put(internalKey, MultiValue.createByType(type));
        }
    }

    public void setKeyComplete(String namespace) {
        keyCompleteNamespaces.add(namespace);
    }

    public void setReadOnly() {
        isReadOnly = true;
    }

    @Nullable
    private <T> T getEntryAndMap(@Nullable String namespace, @Nonnull String key, Function<MultiValue, T> mapper) {
        final MultiValue record = values.get(getInternalKey(namespace, key));
        if (record != null) {
            return mapper.apply(record);
        } else {
            return null;
        }
    }

    @Nonnull
    protected String getNamespaceId(@Nullable String namespace) {
        return StringUtils.defaultIfBlank(namespace, "global");
    }

    @Nonnull
    protected String getInternalKey(@Nullable String namespace, @Nonnull String key) {
        return getNamespaceId(namespace) + INTERNAL_NAMESPACE_SEPARATOR + key;
    }

}
