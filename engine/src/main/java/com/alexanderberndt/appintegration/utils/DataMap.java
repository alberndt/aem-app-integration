package com.alexanderberndt.appintegration.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataMap extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -615425898867657848L;

    public DataMap() {
    }

    public DataMap(Map<? extends String, ?> m) {
        super(m);
    }

    public void setData(@Nonnull String name, @Nullable Object value) {
        if (value != null) {
            this.put(name, value);
        } else {
            this.remove(name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(@Nonnull String name, @Nonnull Class<T> tClass) {
        final Object value = this.get(name);
        if (tClass.isInstance(value)) {
            return (T) value;
        } else {
            return null;
        }
    }

}
