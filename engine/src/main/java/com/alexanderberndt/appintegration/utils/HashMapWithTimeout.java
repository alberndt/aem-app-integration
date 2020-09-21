package com.alexanderberndt.appintegration.utils;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HashMapWithTimeout<K, V> extends AbstractMap<K, V> {

    private static final long DEFAULT_TIMEOUT_MILLIS = 1000L * 60L * 5L;


    private final Map<K, TimestampValue<V>> innerMap = new HashMap<>();

    private final long timeoutMillis;

    public HashMapWithTimeout() {
        this(DEFAULT_TIMEOUT_MILLIS);
    }

    public HashMapWithTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public V get(Object key) {
        final TimestampValue<V> value = innerMap.get(key);
        if ((value != null) && value.isFresh(timeoutMillis)) {
            return value.getValue();
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        final TimestampValue<V> previousValue = innerMap.put(key, new TimestampValue<>(value));
        if ((previousValue != null) && previousValue.isFresh(timeoutMillis)) {
            return previousValue.getValue();
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return innerMap.entrySet().stream()
                .filter(entry -> entry.getValue().isFresh(timeoutMillis))
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue().getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return this.entrySet().equals(((HashMapWithTimeout<?, ?>) o).entrySet());
    }

    @Override
    public int hashCode() {
        return entrySet().hashCode();
    }


    private static class TimestampValue<T> {

        private final T value;

        private final long timestamp;

        public TimestampValue(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public T getValue() {
            return value;
        }

        public boolean isFresh(long timeoutMillis) {
            return (timestamp + timeoutMillis) < System.currentTimeMillis();
        }
    }

}
