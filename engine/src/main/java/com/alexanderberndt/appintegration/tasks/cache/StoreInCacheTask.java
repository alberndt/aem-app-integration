package com.alexanderberndt.appintegration.tasks.cache;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.function.Supplier;

@Component
public class StoreInCacheTask implements ProcessingTask {

    public static final String CACHING_ENABLED_PROP = "caching.enabled";

    @Override
    public void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
        taskContext.setValue(CACHING_ENABLED_PROP, true);
    }

    @Override
    public void process(@Nonnull TaskContext context, @Nonnull ExternalResource resource) {

        final boolean cachingEnabled = context.getValue(CACHING_ENABLED_PROP, true);

        if (cachingEnabled) {
            final ExternalResourceCache cache = context.getExternalResourceCache();
            final Supplier<InputStream> cachedDataSupplier = cache.storeResource(resource);
            resource.setContentSupplier(cachedDataSupplier, InputStream.class);
        } else {
            context.addWarning("Caching disabled!");
        }
    }

}
