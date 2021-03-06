package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;

import javax.annotation.Nonnull;

public class IntegrationLogger extends AbstractLogger {

    public IntegrationLogger(@Nonnull AbstractLogger parentLogger) {
        super(parentLogger);
        this.appender.appendLogger(this);
    }

    public IntegrationLogger(@Nonnull LogAppender appender) {
        super(appender);
        this.appender.appendLogger(this);
    }

    @Nonnull
    @Override
    public String getType() {
        return "integration";
    }

    public ResourceLogger createResourceLogger(@Nonnull final ExternalResourceRef resourceRef) {
        return new ResourceLogger(this, resourceRef);
    }

    public ResourceLogger createResourceLogger(@Nonnull final String url) {
        return new ResourceLogger(this, url);
    }

}
