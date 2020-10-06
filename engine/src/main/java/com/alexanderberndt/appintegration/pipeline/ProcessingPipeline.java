package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.ExternalResourceCache;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.context.GlobalContext;
import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.alexanderberndt.appintegration.pipeline.configuration.Ranking.PIPELINE_EXECUTION;

/**
 * Processing Instance.
 */
public class ProcessingPipeline {

    public static final String CACHING_ENABLED_PROP = "cache:enabled";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final List<TaskWrapper<PreparationTask>> preparationTasks;


    // ToDo: Remove Loading and caching tasks - they are integral part of the pipeline

    // ToDo:

    @Nonnull
    private static final TaskWrapper<LoadingTask> loadingTask = new TaskWrapper<>("download", "download", ProcessingPipeline::download, null);

    @Nonnull
    private final List<TaskWrapper<ProcessingTask>> processingTasks;

    // ToDo: postCacheProcessingTasks

    @Nonnull
    private static final TaskWrapper<PreparationTask> loadFromCacheTask = new TaskWrapper<>("load-from-cache", "load-from-cache", ProcessingPipeline::readFromCache, null);

    @Nonnull
    private static final TaskWrapper<ProcessingTask> storeInCacheTask = new TaskWrapper<>("store-in-cache", "store-in-cache", ProcessingPipeline::storeInCache, null);



    public ProcessingPipeline(
            @Nullable List<TaskWrapper<PreparationTask>> preparationTasks,
            @Nullable List<TaskWrapper<ProcessingTask>> processingTasks) {
        this.preparationTasks = Optional.ofNullable(preparationTasks).orElse(Collections.emptyList());
        this.processingTasks = Optional.ofNullable(processingTasks).orElse(Collections.emptyList());
    }

    public void initContextWithTaskDefaults(@Nonnull GlobalContext<?, ?> context) {
        final ResourceLogger logger = context.getIntegrationLog().createResourceLogger("defaults");
        final DataMap processingData = new DataMap();

        applyWithContext(loadFromCacheTask, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                taskContext -> {
                    taskContext.setValue(CACHING_ENABLED_PROP, true);
                    return null;
                });

        preparationTasks.forEach(taskWrapper ->
                applyWithContext(taskWrapper, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                        taskContext -> {
                            taskWrapper.getTask().declareTaskPropertiesAndDefaults(taskContext);
                            return null;
                        }));

        applyWithContext(loadingTask, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                taskContext -> {
                    loadingTask.getTask().declareTaskPropertiesAndDefaults(taskContext);
                    return null;
                });
        processingTasks.forEach(taskWrapper ->
                applyWithContext(taskWrapper, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                        taskContext -> {
                            taskWrapper.getTask().declareTaskPropertiesAndDefaults(taskContext);
                            return null;
                        }));
    }

    public void initContextWithPipelineConfig(@Nonnull GlobalContext<?, ?> context) {
        final ResourceLogger logger = context.getIntegrationLog().createResourceLogger("pipeline config");
        final DataMap processingData = new DataMap();
        preparationTasks.forEach(taskWrapper ->
                applyWithContext(taskWrapper, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                        taskContext -> this.copyTaskConfiguration(taskWrapper, taskContext)));
        applyWithContext(loadingTask, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                taskContext -> this.copyTaskConfiguration(loadingTask, taskContext));
        processingTasks.forEach(taskWrapper ->
                applyWithContext(taskWrapper, context, logger, Ranking.TASK_DEFAULT, ExternalResourceType.ANY, processingData,
                        taskContext -> this.copyTaskConfiguration(taskWrapper, taskContext)));
    }

    protected <T> Void copyTaskConfiguration(TaskWrapper<T> taskWrapper, TaskContext taskContext) {
        final DataMap configuration = taskWrapper.getConfiguration();
        if ((configuration != null) && !configuration.isEmpty()) {
            configuration.forEach((key, value) -> {
                final String qualifiedKey = StringUtils.prependIfMissing(key, taskWrapper.getId() + TaskContext.NAMESPACE_SEPARATOR);
                taskContext.setValue(qualifiedKey, value);
            });
        }
        return null;
    }


    public ExternalResource loadAndProcessResourceRef(@Nonnull GlobalContext<?, ?> context, @Nonnull ExternalResourceRef resourceRef) {

        final ResourceLogger log = context.getIntegrationLog().createResourceLogger(resourceRef);
        final DataMap processingData = new DataMap();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // preparation tasks
        for (TaskWrapper<PreparationTask> taskWrapper : preparationTasks) {
            final ExternalResourceType resourceType = resourceRef.getExpectedType();
            applyWithContext(taskWrapper, context, log, PIPELINE_EXECUTION, resourceType, processingData,
                    taskContext -> {
                        taskWrapper.getTask().prepare(taskContext, resourceRef);
                        return null;
                    });
        }

        // try to get from cache
        applyWithContext(loadFromCacheTask, context, log, PIPELINE_EXECUTION, resourceRef.getExpectedType(), processingData,
                taskContext -> {
                    loadFromCacheTask.getTask().prepare(taskContext, resourceRef);
                    return null;
                });

        // loading task
        final ExternalResource resource =
                applyWithContext(loadingTask, context, log, PIPELINE_EXECUTION, resourceRef.getExpectedType(), processingData,
                        taskContext -> loadingTask.getTask().load(taskContext, resourceRef));


        // directly return a cached resource
        final ExternalResourceCache cache = context.getExternalResourceCache();
        if (resource.getLoadStatus() == ExternalResource.LoadStatus.CACHED) {
            cache.markResourceRefreshed(resource);
            return resource;
        }

        // processing tasks
        for (TaskWrapper<ProcessingTask> taskWrapper : processingTasks) {
            final ExternalResourceType resourceType = resource.getType();
            applyWithContext(taskWrapper, context, log, PIPELINE_EXECUTION, resourceType, processingData,
                    taskContext -> {
                        taskWrapper.getTask().process(taskContext, resource);
                        return null;
                    });
        }

        applyWithContext(storeInCacheTask, context, log, PIPELINE_EXECUTION, resource.getType(), processingData,
                taskContext -> {
                    storeInCacheTask.getTask().process(taskContext, resource);
                    return null;
                });

        // ToDo: Check this!
//        // store resource is cache
//        cache.storeResource(resource);

        log.setTime(String.format("%,d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
        return resource;
    }

    protected static void readFromCache(@Nonnull TaskContext taskContext, @Nonnull ExternalResourceRef resourceRef) {
        final boolean cachingEnabled = taskContext.getValue(CACHING_ENABLED_PROP, true);
        if (cachingEnabled) {
            final ExternalResourceCache cache = taskContext.getExternalResourceCache();
            if (cache != null) {
                final ExternalResource cachedRes = cache.getCachedResource(resourceRef, taskContext.getResourceFactory());
                if (cachedRes != null) {
                    resourceRef.setCachedExternalRes(cachedRes);
                }
            } else {
                taskContext.addWarning("No cache available!");
            }
        } else {
            taskContext.addWarning("Caching disabled!");
        }
    }

    protected static void storeInCache(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource) {

        final boolean cachingEnabled = taskContext.getValue(CACHING_ENABLED_PROP, true);

        if (cachingEnabled) {
            final ExternalResourceCache cache = taskContext.getExternalResourceCache();
            if (cache != null) {
                final Supplier<InputStream> cachedDataSupplier = cache.storeResource(resource);
                resource.setContentSupplier(cachedDataSupplier, InputStream.class);
            } else {
                taskContext.addWarning("No cache available!");
            }
        } else {
            taskContext.addWarning("Caching disabled!");
        }
    }



    protected static ExternalResource download(@Nonnull TaskContext context, ExternalResourceRef resourceRef) {

//        final ExternalResource cachedResource = resourceRef.getCachedExternalRes();
//        if (cachedResource != null) {
//            return cachedResource;
//        }

        // ToDo: Cache-Logic should be part of the resource loader

        ResourceLoader resourceLoader = context.getResourceLoader();
        try {
            return resourceLoader.load(resourceRef, context.getResourceFactory());
        } catch (IOException | ResourceLoaderException e) {
            throw new AppIntegrationException("Failed to load resource " + resourceRef.getUri(), e);
        }
    }

    protected <T, R> R applyWithContext(
            @Nonnull TaskWrapper<T> taskWrapper,
            @Nonnull GlobalContext<?, ?> context,
            @Nonnull ResourceLogger logger,
            @Nonnull Ranking ranking,
            @Nonnull ExternalResourceType resourceType,
            @Nonnull DataMap processingData,
            @Nonnull Function<TaskContext, R> function) {

        LOG.debug("call with context for task {}", taskWrapper.getId());
        final TaskLogger taskLogger = logger.createTaskLogger(taskWrapper.getId(), taskWrapper.getName());
        final TaskContext taskContext = context.createTaskContext(taskLogger, ranking, taskWrapper.getId(), resourceType, processingData);
        return function.apply(taskContext);
    }

}
