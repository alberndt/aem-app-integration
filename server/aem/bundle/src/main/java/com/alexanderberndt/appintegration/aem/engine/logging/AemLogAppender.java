package com.alexanderberndt.appintegration.aem.engine.logging;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.alexanderberndt.appintegration.engine.logging.appender.AbstractLogAppender;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AemLogAppender extends AbstractLogAppender<AemLogAppender.AemLogEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String MESSAGE_PROP = "message";
    public static final String STATUS_PROP = "status";
    public static final String TYPE_PROP = "type";

    public static final String MESSAGE_TYPE = "message";

    private final Resource rootRes;

    private final ResourceResolver resolver;

    public AemLogAppender(Resource rootRes) {
        this.rootRes = rootRes;
        this.resolver = rootRes.getResourceResolver();
    }

    @Nullable
    @Override
    protected AemLogEntry createNewLogEntry(@Nullable AemLogEntry parentEntry, @Nonnull AbstractLogger logger) {

        final Resource parentRes = (parentEntry != null) ? parentEntry.logResource : rootRes;

        try {
            // create log-resource
            final String entryName = ResourceUtil.createUniqueChildName(parentRes, JcrUtil.escapeIllegalJcrChars(logger.getLoggerName()));
            final Resource logRes = resolver.create(parentRes, entryName, Collections.singletonMap(TYPE_PROP, logger.getType()));
            return new AemLogEntry(logRes);
        } catch (PersistenceException e) {
            LOG.error("Cannot create log-entry at path {}", parentRes.getPath(), e);
            return null;
        }
    }

    @Override
    protected void createNewLogMessage(@Nullable AemLogEntry parentEntry, @Nullable LogStatus status, @Nullable String message) {

        final Resource parentRes = (parentEntry != null) ? parentEntry.logResource : rootRes;

        try {
            // create log-resource
            final String entryName = ResourceUtil.createUniqueChildName(parentRes, MESSAGE_TYPE);
            final Map<String, Object> properties = new HashMap<>();
            properties.put(TYPE_PROP, MESSAGE_TYPE);
            if (status != null) {
                properties.put(STATUS_PROP, status.toString());
            }
            if (message != null) {
                properties.put(MESSAGE_PROP, message);
            }
            resolver.create(parentRes, entryName, properties);
        } catch (PersistenceException e) {
            LOG.error("Cannot create log-message at path {}", parentRes.getPath(), e);
        }
    }


    @Override
    public void close() {
        // empty, not needed
    }

    protected static class AemLogEntry implements AbstractLogAppender.LogEntry<AemLogEntry> {

        final Resource logResource;

        final ModifiableValueMap valueMap;

        public AemLogEntry(Resource logResource) {
            this.logResource = logResource;
            this.valueMap = this.logResource.adaptTo(ModifiableValueMap.class);
        }

        @Override
        public void setSummary(LogStatus status, String message) {
            setProperty(STATUS_PROP, (status != null) ? status.toString() : null);
            setProperty(MESSAGE_PROP, message);
        }

        @Override
        public void setStatus(LogStatus status) {
            setProperty(STATUS_PROP, (status != null) ? status.toString() : null);
        }

        @Override
        public void setProperty(String key, String value) {
            if (value != null) {
                valueMap.put(key, value);
            } else {
                valueMap.remove(key);
            }
        }

    }
}
