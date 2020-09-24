package com.alexanderberndt.appintegration.engine.logging;

import javax.annotation.Nonnull;

public class DetailsLogger extends AbstractLogger {

    public DetailsLogger(@Nonnull AbstractLogger parentLogger) {
        super(parentLogger);
        this.appender.appendLogger(this);
    }

    @Nonnull
    @Override
    public String getType() {
        return "details";
    }


}
