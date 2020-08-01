package com.alexanderberndt.appintegration.pipeline.valuemap;

import com.alexanderberndt.appintegration.pipeline.context.Context;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import static com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap.NAMESPACE_SEPARATOR;

public class ScopedValueMapFacade {

    private final Context context;

    private final ValueMap valueMap;

    public ScopedValueMapFacade(Context context, ValueMap valueMap) {
        this.context = context;
        this.valueMap = valueMap;
    }

    NamespaceKey getNamespaceKey(@Nonnull String key) {
        final int splitIndex = key.indexOf(NAMESPACE_SEPARATOR);
        if (splitIndex > 0) {
            return new NamespaceKey(key.substring(0, splitIndex), key.substring(splitIndex + NAMESPACE_SEPARATOR.length()));
        } else {
            return new NamespaceKey(context.getNamespace(), key);
        }
    }

    public Object getValue(@Nonnull String key) {
        final NamespaceKey nk = getNamespaceKey(key);
        return valueMap.getValue(nk.namespace, nk.key);
    }

    public <T> T getValue(@Nonnull String key, @Nonnull Class<T> type) {
        try {
            final NamespaceKey nk = getNamespaceKey(key);
            return valueMap.getValue(nk.namespace, nk.key, type);
        } catch (ValueException e) {
            context.addWarning(e.getMessage());
            return null;
        }
    }

    public <T> T getValue(@Nonnull String key, @Nonnull T defaultValue) {
        try {
            final NamespaceKey nk = getNamespaceKey(key);
            return valueMap.getValue(nk.namespace, nk.key, defaultValue);
        } catch (ValueException e) {
            context.addWarning(e.getMessage());
            return null;
        }
    }

    public void setValue(@Nonnull String key, Object value) {
        try {
            final NamespaceKey nk = getNamespaceKey(key);
            valueMap.setValue(nk.namespace, nk.key, context.getRank(), value);
        } catch (ValueException e) {
            context.addWarning(e.getMessage());
        }
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return valueMap.entrySet(context.getNamespace());
    }

    public void setKeyComplete() {
        valueMap.setKeyComplete(context.getNamespace());
    }

    static class NamespaceKey {

        private final String namespace;
        private final String key;

        public NamespaceKey(String namespace, String key) {
            this.namespace = namespace;
            this.key = key;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getKey() {
            return key;
        }
    }
}