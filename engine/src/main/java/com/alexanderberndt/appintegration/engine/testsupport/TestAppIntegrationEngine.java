package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.engine.AbstractAppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.AppIntegrationEngine;
import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TestAppIntegrationEngine extends AbstractAppIntegrationEngine<TestAppInstance, TestGlobalContext> implements AppIntegrationEngine<TestAppInstance> {

    @Nonnull
    private final TestAppIntegrationFactory factory;

    @Nonnull
    private final Supplier<LogAppender> appenderSupplier;

    private final Map<String, TestExternalResourceCache> resourceCacheMap = new HashMap<>();

    public TestAppIntegrationEngine() {
        this(new TestAppIntegrationFactory(), Slf4jLogAppender::new);
    }

    public TestAppIntegrationEngine(@Nonnull TestAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Override
    public ExternalResource getHtmlSnippet(@Nonnull TestAppInstance instance) {
        return super.getHtmlSnippet(createGlobalContext(instance.getApplicationId()), instance);
    }

    @Override
    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {
        return super.getStaticResource(createGlobalContext(applicationId), relativePath);
    }

    @Override
    public boolean isDynamicPath(@Nonnull String applicationId, String relativePath) {
        return super.isDynamicPath(createGlobalContext(applicationId), relativePath);
    }

    @Override
    public List<String> getDynamicPaths(@Nonnull String applicationId) {
        return super.getDynamicPaths(createGlobalContext(applicationId));
    }

    @Override
    public void prefetch(@Nonnull List<TestAppInstance> applicationInstanceList) {
        groupInstancesByApplicationId(applicationInstanceList,
                (applicationId, groupedInstanceList) ->
                        super.prefetch(createGlobalContext(applicationId), groupedInstanceList));
    }

    public TestExternalResourceCache getExternalResourceCache(String applicationId) {
        return resourceCacheMap.computeIfAbsent(applicationId, id -> new TestExternalResourceCache());
    }

    @Nonnull
    public TestAppIntegrationFactory getFactory() {
        return factory;
    }

    @Nonnull
    protected TestGlobalContext createGlobalContext(String applicationId) {
        final ExternalResourceCache cache = getExternalResourceCache(applicationId);
        return new TestGlobalContext(applicationId, factory, cache, appenderSupplier.get());
    }

}
