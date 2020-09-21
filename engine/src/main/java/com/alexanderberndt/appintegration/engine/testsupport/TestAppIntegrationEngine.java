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

    private final Map<String, ExternalResourceCache> resourceCacheMap = new HashMap<>();

    public TestAppIntegrationEngine() {
        this(new TestAppIntegrationFactory(), Slf4jLogAppender::new);
    }

    public TestAppIntegrationEngine(@Nonnull TestAppIntegrationFactory factory, @Nonnull final Supplier<LogAppender> appenderSupplier) {
        this.factory = factory;
        this.appenderSupplier = appenderSupplier;
    }

    @Override
    public ExternalResource getHtmlSnippet(@Nonnull TestAppInstance instance) {
        final String applicationId = instance.getApplicationId();
        final TestGlobalContext context = new TestGlobalContext(applicationId, factory, appenderSupplier.get());
        return super.getHtmlSnippet(context, instance);
    }

    @Override
    public ExternalResource getStaticResource(@Nonnull String applicationId, @Nonnull String relativePath) {
        final TestGlobalContext context = new TestGlobalContext(applicationId, factory, appenderSupplier.get());
        return super.getStaticResource(context, relativePath);
    }

    @Override
    public boolean isDynamicPath(@Nonnull String applicationId, String relativePath) {
        final TestGlobalContext context = new TestGlobalContext(applicationId, factory, appenderSupplier.get());
        return super.isDynamicPath(context, relativePath);
    }

    @Override
    public List<String> getDynamicPaths(@Nonnull String applicationId) {
        final TestGlobalContext context = new TestGlobalContext(applicationId, factory, appenderSupplier.get());
        return super.getDynamicPaths(context);
    }

    @Override
    public void prefetch(@Nonnull List<TestAppInstance> applicationInstanceList) {
        groupInstancesByApplicationId(applicationInstanceList,
                (applicationId, groupedInstanceList) -> {
                    final TestGlobalContext context = new TestGlobalContext(applicationId, factory, appenderSupplier.get());
                    super.prefetch(context, groupedInstanceList);
                });
    }

    public void setExternalResourceCache(String applicationId, ExternalResourceCache cache) {
        resourceCacheMap.put(applicationId, cache);
    }

    public ExternalResourceCache getExternalResourceCache(String applicationId) {
        return resourceCacheMap.get(applicationId);
    }

    @Nonnull
    public TestAppIntegrationFactory getFactory() {
        return factory;
    }
}
