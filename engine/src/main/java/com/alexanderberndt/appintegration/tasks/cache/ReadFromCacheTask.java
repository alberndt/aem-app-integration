package com.alexanderberndt.appintegration.tasks.cache;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;

@Component
public class ReadFromCacheTask implements PreparationTask {

    public static final String CACHING_ENABLED_PROP = "caching.enabled";

    @Override
    public void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
        taskContext.setValue(CACHING_ENABLED_PROP, true);
    }

    @Override
    public void prepare(@Nonnull TaskContext context, @Nonnull ExternalResourceRef resourceRef) {

        final boolean cachingEnabled = context.getValue(CACHING_ENABLED_PROP, true);

        if (cachingEnabled) {
            final ExternalResourceCache cache = context.getExternalResourceCache();
            final ExternalResource cachedRes = cache.getCachedResource(resourceRef, context.getResourceFactory());
            if (cachedRes != null) {
                resourceRef.setCachedExternalRes(cachedRes);
            }
        } else {
            context.addWarning("Caching disabled!");
        }
    }

}
