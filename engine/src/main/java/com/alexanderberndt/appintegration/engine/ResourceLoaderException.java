package com.alexanderberndt.appintegration.engine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

public class ResourceLoaderException extends Exception{

    private static final long serialVersionUID = -1536114261047936664L;

    public enum FailedReason {NOT_FOUND, ERROR}

    @Nonnull
    private final FailedReason reason;

    @Nullable
    private final Map<String, Serializable> loadStatusDetails;


    public ResourceLoaderException(@Nonnull FailedReason reason, String message) {
        this(reason, message, (Map<String, Serializable>) null);
    }

    @SuppressWarnings("unused")
    public ResourceLoaderException(@Nonnull FailedReason reason, String message, Throwable cause) {
        this(reason, message, null, cause);
    }

    public ResourceLoaderException(@Nonnull FailedReason reason, @Nonnull String message, @Nullable Map<String, Serializable> loadStatusDetails) {
        super(message);
        this.reason = reason;
        this.loadStatusDetails = loadStatusDetails;
    }

    public ResourceLoaderException(@Nonnull FailedReason reason, @Nonnull String message, @Nullable Map<String, Serializable> loadStatusDetails, Throwable cause) {
        super(message, cause);
        this.reason = reason;
        this.loadStatusDetails = loadStatusDetails;
    }

    @Nonnull
    public FailedReason getReason() {
        return reason;
    }

    @Nullable
    public Map<String, Serializable> getLoadStatusDetails() {
        return loadStatusDetails;
    }
}
