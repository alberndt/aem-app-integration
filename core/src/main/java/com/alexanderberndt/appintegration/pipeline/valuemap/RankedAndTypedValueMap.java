package com.alexanderberndt.appintegration.pipeline.valuemap;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RankedAndTypedValueMap {

    private static final String INTERNAL_NAMESPACE_SEPARATOR = "#°#";

    private final Map<String, RankedAndTypedValue> values = new HashMap<>();

    private final Set<String> keyCompleteNamespaces = new HashSet<>();

    private final EnumSet<Ranking> readOnlyRankSet = EnumSet.noneOf(Ranking.class);

    public Object getValue(@Nullable String namespace, @Nonnull String key) {
        return getEntryAndMap(namespace, key, RankedAndTypedValue::getValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nullable String namespace, @Nonnull String key, @Nonnull Class<T> type) throws ValueException {
        final RankedAndTypedValue rankedAndTypedValue = getEntryAndMap(namespace, key, Function.identity());
        if (rankedAndTypedValue == null) {
            return null;
        } else {
            final Class<?> expectedType = rankedAndTypedValue.getType();
            if ((expectedType == null) || (expectedType == type)) {
                return (T) rankedAndTypedValue.getValue();
            } else {
                throw new ValueException(String.format("parameter %s is requested as %s, but is %s!",
                        key, type.getSimpleName(), rankedAndTypedValue.getType().getSimpleName()));
            }
        }
    }

    @Nonnull
    public <T> T requireValue(@Nullable String namespace, String key, Class<T> type) throws ValueException {
        final T value = getValue(namespace, key, type);
        if (value != null) {
            return value;
        } else {
            throw new ValueException(String.format("missing required parameter %s!", key));
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nullable String namespace, @Nonnull String key, @Nonnull T defaultValue) throws ValueException {
        T value = (T) getValue(namespace, key, defaultValue.getClass());
        return (value != null) ? value : defaultValue;
    }


    public Class<?> getType(@Nullable String namespace, @Nonnull String key) {
        return getEntryAndMap(namespace, key, RankedAndTypedValue::getType);
    }

    public String getTypeName(@Nullable String namespace, @Nonnull String key) {
        return getEntryAndMap(namespace, key, RankedAndTypedValue::getTypeName);
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
     * @throws ValueException Thrown, if value was not set
     */
    public void setValue(@Nullable String namespace, @Nonnull String key, @Nonnull Ranking rank, @Nullable Object value) throws ValueException {

        if (readOnlyRankSet.contains(rank))
            throw new ValueException(String.format("Rank %s is read-only. Cannot set value!", rank));

        final String internalKey = getInternalKey(namespace, key);
        final RankedAndTypedValue existingRecord = values.get(internalKey);
        if (existingRecord != null) {
            existingRecord.setValue(rank, value);
        } else {
            if (!keyCompleteNamespaces.contains(namespace)) {
                values.put(internalKey, RankedAndTypedValue.createByValue(rank, value));
            } else {
                throw new ValueException(String.format("Variable %s is not allowed (namespace %s is key-complete)!",
                        internalKey, getNamespaceId(namespace)));
            }
        }
    }

    public void setType(@Nullable String namespace, @Nonnull String key, @Nonnull Ranking rank, @Nonnull Class<?> type) throws ValueException {

        if (readOnlyRankSet.contains(rank))
            throw new ValueException(String.format("Rank %s is read-only. Cannot set type!", rank));

        final String internalKey = getInternalKey(namespace, key);
        final RankedAndTypedValue existingRecord = values.get(internalKey);
        if (existingRecord != null) {
            existingRecord.setType(rank, type);
        } else {
            values.put(internalKey, RankedAndTypedValue.createByType(rank, type));
        }
    }

    public void setKeyComplete(String namespace) {
        keyCompleteNamespaces.add(namespace);
    }

    public void clear(Ranking rank) {
        this.values.forEach((key, value) -> value.clear(rank));
    }

    public void setReadOnly(Ranking rank) {
        readOnlyRankSet.add(rank);
    }

    @Nullable
    private <T> T getEntryAndMap(@Nullable String namespace, @Nonnull String key, Function<RankedAndTypedValue, T> mapper) {
        final RankedAndTypedValue record = values.get(getInternalKey(namespace, key));
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
