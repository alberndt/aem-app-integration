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
import java.util.function.Consumer;
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
    private final List<TaskWrapper<ProcessingTask>> processingTasks;

    // ToDo: postCacheProcessingTasks


    public ProcessingPipeline(
            @Nullable List<TaskWrapper<PreparationTask>> preparationTasks,
            @Nullable List<TaskWrapper<ProcessingTask>> processingTasks) {
        this.preparationTasks = Optional.ofNullable(preparationTasks).orElse(Collections.emptyList());
        this.processingTasks = Optional.ofNullable(processingTasks).orElse(Collections.emptyList());
    }

    public void initContextWithTaskDefaults(@Nonnull GlobalContext<?, ?> context) {

        final PipelineContext pipelineContext = new PipelineContext(context, Ranking.TASK_DEFAULT, "Task defaults");

        // init pipeline global properties
        pipelineContext.acceptWithContext("pipeline", "pipeline",
                taskContext -> taskContext.setValue(CACHING_ENABLED_PROP, true));

        // declare task properties
        preparationTasks.forEach(taskWrapper ->
                pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                        taskContext -> taskWrapper.getTask().declareTaskPropertiesAndDefaults(taskContext)));
        processingTasks.forEach(taskWrapper ->
                pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                        taskContext -> taskWrapper.getTask().declareTaskPropertiesAndDefaults(taskContext)));
    }

    public void initContextWithPipelineConfig(@Nonnull GlobalContext<?, ?> context) {

        final PipelineContext pipelineContext = new PipelineContext(context, Ranking.PIPELINE_DEFINITION, "Pipeline config");

        preparationTasks.forEach(taskWrapper ->
                pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                        taskContext -> this.copyTaskConfiguration(taskWrapper, taskContext)));
        processingTasks.forEach(taskWrapper ->
                pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                        taskContext -> this.copyTaskConfiguration(taskWrapper, taskContext)));
    }

    protected void copyTaskConfiguration(TaskWrapper<?> taskWrapper, TaskContext taskContext) {
        final DataMap configuration = taskWrapper.getConfiguration();
        if ((configuration != null) && !configuration.isEmpty()) {
            configuration.forEach((key, value) -> {
                final String qualifiedKey = StringUtils.prependIfMissing(key, taskWrapper.getId() + TaskContext.NAMESPACE_SEPARATOR);
                taskContext.setValue(qualifiedKey, value);
            });
        }
    }


    public ExternalResource loadAndProcessResourceRef(@Nonnull GlobalContext<?, ?> context, @Nonnull ExternalResourceRef resourceRef) {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final PipelineContext pipelineContext = new PipelineContext(context, PIPELINE_EXECUTION, resourceRef);


        // preparation tasks
        for (TaskWrapper<PreparationTask> taskWrapper : preparationTasks) {
            pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                    taskContext -> taskWrapper.getTask().prepare(taskContext, resourceRef));
        }

        // try to get from cache
        pipelineContext.acceptWithContext("load-from-cache", "load-from-cache",
                taskContext -> ProcessingPipeline.readFromCache(taskContext, resourceRef));

        // loading task
        final ExternalResource resource = pipelineContext.applyWithContext("download", "download",
                taskContext -> ProcessingPipeline.download(taskContext, resourceRef));


        // directly return a cached resource
        final ExternalResourceCache cache = context.getExternalResourceCache();
        if (resource.getLoadStatus() == ExternalResource.LoadStatus.CACHED) {
            cache.markResourceRefreshed(resource);
            return resource;
        }

        // processing tasks
        for (TaskWrapper<ProcessingTask> taskWrapper : processingTasks) {
            pipelineContext.acceptWithContext(taskWrapper.getId(), taskWrapper.getName(),
                    taskContext -> taskWrapper.getTask().process(taskContext, resource));
        }

        pipelineContext.acceptWithContext("store-in-cache", "store-in-cache",
                taskContext -> ProcessingPipeline.storeInCache(taskContext, resource));

        pipelineContext.getResourceLogger().setTime(String.format("%,d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
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


    private static class PipelineContext {

        @Nonnull
        private final GlobalContext<?, ?> context;

        @Nonnull
        private final Ranking ranking;

        @Nullable
        private final ExternalResourceRef resourceRef;

        @Nonnull
        private final ResourceLogger logger;

        @Nonnull
        private final DataMap processingData = new DataMap();

        public PipelineContext(@Nonnull GlobalContext<?, ?> context, @Nonnull Ranking ranking, @Nonnull String resourceName) {
            this.context = context;
            this.ranking = ranking;
            this.resourceRef = null;
            this.logger = context.getIntegrationLog().createResourceLogger(resourceName);
        }

        public PipelineContext(@Nonnull GlobalContext<?, ?> context, @Nonnull Ranking ranking, @Nonnull ExternalResourceRef resourceRef) {
            this.context = context;
            this.ranking = ranking;
            this.resourceRef = resourceRef;
            this.logger = context.getIntegrationLog().createResourceLogger(resourceRef);
        }

        public ResourceLogger getResourceLogger() {
            return logger;
        }

        public <R> R applyWithContext(
                @Nonnull String taskId,
                @Nonnull String taskName,
                @Nonnull Function<TaskContext, R> function) {

            LOG.debug("call with context for task {}", taskId);
            final TaskLogger taskLogger = logger.createTaskLogger(taskId, taskName);
            final ExternalResourceType resourceType = (resourceRef != null) ? resourceRef.getExpectedType() : ExternalResourceType.ANY;
            final TaskContext taskContext = context.createTaskContext(taskLogger, ranking, taskId, resourceType, processingData);
            return function.apply(taskContext);
        }

        public void acceptWithContext(
                @Nonnull String taskId,
                @Nonnull String taskName,
                @Nonnull Consumer<TaskContext> consumer) {

            this.applyWithContext(taskId, taskName, taskContext -> {
                consumer.accept(taskContext);
                return null;
            });
        }

    }

}
