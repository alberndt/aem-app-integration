package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestAppIntegrationEngine extends AppIntegrationEngine<TestAppInstance, TestGlobalContext> {

    @Nonnull
    private final TestAppIntegrationFactory factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    private final Map<String, ExternalResourceCache> resourceCacheMap = new HashMap<>();

    public TestAppIntegrationEngine(@Nonnull TestAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Nonnull
    @Override
    protected AppIntegrationFactory<TestAppInstance, TestGlobalContext> getFactory() {
        return factory;
    }

    @Override
    protected <R> R callWithGlobalContext(String applicationId, Function<TestGlobalContext, R> function) {
        return function.apply(new TestGlobalContext(appenderSupplier.get()));
    }

    @Override
    protected <R> R callWithExternalResourceCache(String applicationId, Function<ExternalResourceCache, R> function) {
        return function.apply(resourceCacheMap.computeIfAbsent(applicationId, id -> new TestExternalResourceCache()));
    }

    public void setExternalResourceCache(String applicationId, ExternalResourceCache cache) {
        resourceCacheMap.put(applicationId, cache);
    }

    public ExternalResourceCache getExternalResourceCache(String applicationId) {
        return resourceCacheMap.get(applicationId);
    }

}
