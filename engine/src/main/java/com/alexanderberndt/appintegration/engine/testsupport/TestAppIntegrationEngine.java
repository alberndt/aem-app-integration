package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestAppIntegrationEngine extends AppIntegrationEngine<TestAppInstance, TestGlobalContext> {

    @Nonnull
    private final AppIntegrationFactory<TestAppInstance, TestGlobalContext> factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    private final Map<String, ExternalResourceCache> resourceCacheMap = new HashMap<>();

    public TestAppIntegrationEngine() {
        this(new TestAppIntegrationFactory(), Slf4jLogAppender::new);
    }

    public TestAppIntegrationEngine(@Nonnull AppIntegrationFactory<TestAppInstance, TestGlobalContext> factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Override
    protected <R> R callRuntimeMethodWithContext(@Nonnull String applicationId, @Nonnull Function<TestGlobalContext, R> function) {
        return function.apply(new TestGlobalContext(applicationId, factory, appenderSupplier.get()));
    }

    @Override
    protected void callBackgroundMethodWithContext(@Nonnull String applicationId, @Nonnull Consumer<TestGlobalContext> consumer) {
        consumer.accept(new TestGlobalContext(applicationId, factory, appenderSupplier.get()));
    }


    public void setExternalResourceCache(String applicationId, ExternalResourceCache cache) {
        resourceCacheMap.put(applicationId, cache);
    }

    public ExternalResourceCache getExternalResourceCache(String applicationId) {
        return resourceCacheMap.get(applicationId);
    }

}
