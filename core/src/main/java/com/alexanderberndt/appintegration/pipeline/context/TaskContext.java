package com.alexanderberndt.appintegration.pipeline.context;

import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoaderFactory;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Set;

public abstract class TaskContext<T extends GenericTask<T>> {

    public static final String NAMESPACE_SEPARATOR = ":";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final T task;

    private final GlobalContext globalContext;

    private final String taskNamespace;

    private final String messagePrefix;

    private Ranking rank = Ranking.TASK_DEFAULT;

    protected TaskContext(@Nonnull GlobalContext globalContext, @Nonnull T task, @Nonnull String taskNamespace, String humanReadableTaskName) {
        this.task = task;
        this.globalContext = globalContext;
        this.taskNamespace = taskNamespace;
        // ToDo: Connect with parent-context
        this.messagePrefix = String.format("%s (%s): ", humanReadableTaskName, taskNamespace);
    }

    public T getTask() {
        return task;
    }

    public void addWarning(String message) {
        globalContext.addWarning(messagePrefix + message);
    }

    public void addError(String message) {
        globalContext.addError(messagePrefix + message);
    }

    public void declareTaskPropertiesAndDefaults() {
        this.task.declareTaskPropertiesAndDefaults(this);
    }


    @Nonnull
    public ResourceLoaderFactory getResourceLoaderFactory() {
        throw new UnsupportedOperationException("Not yet implemented!");
        //return globalContext.getResourceLoaderFactory();
    }

    public Ranking getRank() {
        return rank;
    }

    protected void setRank(Ranking rank) {
        this.rank = rank;
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

    protected class NamespaceKey {

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
