package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Set;

public class TaskContext {

    public static final String NAMESPACE_SEPARATOR = ":";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GlobalContext globalContext;

    private final Ranking rank;

    private final String taskNamespace;

    protected TaskContext(GlobalContext globalContext, Ranking rank, String taskNamespace) {
        this.globalContext = globalContext;
        this.rank = rank;
        this.taskNamespace = taskNamespace;
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
        return globalContext.getProcessingParams().getValue(nk.namespace, nk.key);
    }

    public <T> T getValue(@Nonnull String key, @Nonnull Class<T> type) {
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            return globalContext.getProcessingParams().getValue(nk.namespace, nk.key, type);
        } catch (ValueException e) {
            addWarning(e.getMessage());
            return null;
        }
    }

    @Nonnull
    public <T> T getValue(@Nonnull String key, @Nonnull T defaultValue) {
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            return globalContext.getProcessingParams().getValue(nk.namespace, nk.key, defaultValue);
        } catch (ValueException e) {
            addWarning(e.getMessage());
            return defaultValue;
        }
    }

    public void setValue(@Nonnull String key, Object value) {
        LOG.debug("setValue({}, {}, {}) @ {}", rank, key, value, this.taskNamespace);
        try {
            final NamespaceKey nk = parseNamespaceKey(key);
            globalContext.getProcessingParams().setValue(nk.namespace, nk.key, rank, value);
        } catch (ValueException e) {
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
            globalContext.getProcessingParams().setType(nk.namespace, nk.key, rank, type);
        } catch (ValueException e) {
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
        final int splitIndex = key.indexOf(NAMESPACE_SEPARATOR);
        if (splitIndex > 0) {
            return new NamespaceKey(key.substring(0, splitIndex), key.substring(splitIndex + NAMESPACE_SEPARATOR.length()));
        } else {
            return new NamespaceKey(this.taskNamespace, key);
        }
    }

    protected static class NamespaceKey {

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
