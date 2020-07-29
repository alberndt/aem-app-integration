package com.alexanderberndt.appintegration.utils;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ValueMap implements Map<String, Object> {

    public static final ValueMap EMPTY_VALUE_MAP = new ValueMap(false);

    private final ValueMap parent;

    private final String qualifier;

    private final Map<String, Object> innerValueMap;

    public ValueMap(boolean isModifiable) {
        this(null, null, Collections.emptyMap(), isModifiable);
    }

    public ValueMap(Map<? extends String, ?> values, boolean isModifiable) {
        this(null, null, values, isModifiable);
    }

    public ValueMap(ValueMap parent, String qualifier, Map<? extends String, ?> values, boolean isModifiable) {
        this.parent = parent;
        this.qualifier = StringUtils.defaultIfBlank(qualifier, null);
        if (isModifiable) {
            this.innerValueMap = new HashMap<>(values);
        } else {
            this.innerValueMap = Collections.unmodifiableMap(new HashMap<>(values));
        }
    }


    @Override
    public Object get(@Nonnull Object key) {
        if (this.innerValueMap.containsKey(key)) {
            return this.innerValueMap.get(key);
        } else {
            if (parent != null) {
                // fallback 1, return parent with qualified parameter
                if (qualifier != null) {
                    final Object parentValue = parent.get(qualifier + "." + key);
                    if (parentValue != null) {
                        return parentValue;
                    }
                }
                // fallback 2, return parent without qualification
                return parent.get(key);
            } else {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull String name, @Nonnull Class<T> type) {
        final Object value = this.get(name);
        if (value == null) {
            return null;
        } else if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else {
            throw new AppIntegrationException(String.format("parameter %s is requested as %s, but is %s!",
                    name, type.getSimpleName(), value.getClass().getSimpleName()));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull String name, @Nonnull T defaultValue) {
        T value = (T) get(name, defaultValue.getClass());
        return (value != null) ? value : defaultValue;
    }

    public <T> T require(String name, Class<T> type) {
        final T value = get(name, type);
        if (value != null) {
            return value;
        } else {
            throw new AppIntegrationException(String.format("missing required parameter %s!", name));
        }
    }

    public <T> T require(String name, T defaultValue) {
        final T value = get(name, defaultValue);
        if (value != null) {
            return value;
        } else {
            throw new AppIntegrationException(String.format("missing required parameter %s!", name));
        }
    }

    @Override
    public int size() {
        return innerValueMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerValueMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return innerValueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return innerValueMap.containsValue(value);
    }

    @Override
    public Object put(String key, Object value) {
        return innerValueMap.put(key, value);
    }

    @Override
    public Object remove(@Nonnull Object key) {
        return innerValueMap.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ?> m) {
        innerValueMap.putAll(m);
    }

    @Override
    public void clear() {
        innerValueMap.clear();
    }

    @Override
    @Nonnull
    public Set<String> keySet() {
        return innerValueMap.keySet();
    }

    @Override
    @Nonnull
    public Collection<Object> values() {
        return innerValueMap.values();
    }

    @Override
    @Nonnull
    public Set<Entry<String, Object>> entrySet() {
        return innerValueMap.entrySet();
    }

    public ValueMap getParentPredefinedValues() {
        if ((parent != null) && (qualifier != null)) {
            final String prefix = qualifier + ".";
            Map<String, Object> predefinedValues = parent.entrySet().stream()
                    .filter(entry -> StringUtils.startsWith(entry.getKey(), prefix))
                    .collect(Collectors.toMap(entry -> StringUtils.removeStart(entry.getKey(), prefix), Entry::getValue));
            return new ValueMap(predefinedValues, false);
        } else {
            return EMPTY_VALUE_MAP;
        }
    }
}
