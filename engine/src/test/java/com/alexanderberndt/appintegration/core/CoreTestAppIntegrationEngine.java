package com.alexanderberndt.appintegration.core;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoreTestAppIntegrationEngine extends AppIntegrationEngine<CoreTestAppInstance, CoreTestGlobalContext> {

    @Nonnull
    private final CoreAppIntegrationFactory factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    public CoreTestAppIntegrationEngine(@Nonnull CoreAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Nonnull
    @Override
    protected AppIntegrationFactory<CoreTestAppInstance, CoreTestGlobalContext> getFactory() {
        return factory;
    }

    @Override
    protected void callWithGlobalContext(String applicationId, Consumer<CoreTestGlobalContext> consumer) {
        consumer.accept(new CoreTestGlobalContext(appenderSupplier.get()));

    }

}
