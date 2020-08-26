package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.builder.BasicPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.PipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alexanderberndt.appintegration.pipeline.configuration.Ranking.PIPELINE_EXECUTION;

/**
 * Processing Instance.
 */
public class ProcessingPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GlobalContext context;

    private final List<TaskInstance<PreparationTask>> preparationTasks;

    private final TaskInstance<LoadingTask> loadingTask;

    private final List<TaskInstance<ProcessingTask>> processingTasks;

    public ProcessingPipeline(GlobalContext context,
                              List<TaskInstance<PreparationTask>> preparationTasks,
                              TaskInstance<LoadingTask> loadingTask,
                              List<TaskInstance<ProcessingTask>> processingTasks) {
        this.context = context;
        this.preparationTasks = preparationTasks;
        this.loadingTask = loadingTask;
        this.processingTasks = processingTasks;
    }


    @Deprecated
    public static BasicPipelineBuilder createPipelineInstance(@Nonnull GlobalContext context, @Nonnull ResourceLogger pipelineLog) {
        return new BasicPipelineBuilder(context, pipelineLog);
    }

    @Deprecated
    public static PipelineBuilder createPipelineInstance(@Nonnull GlobalContext context, @Nonnull TaskFactory taskFactory, @Nonnull ResourceLogger pipelineLog) {
        return new PipelineBuilder(context, taskFactory, pipelineLog);
    }


    public ExternalResource loadAndProcessResourceRef(ExternalResourceRef resourceRef, ExternalResourceFactory factory) throws IOException {

        validatePipeline();

        final ResourceLogger log = context.getIntegrationLog().createResourceLogger(resourceRef);
        final Map<String, Object> processingData = new HashMap<>();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // preparation tasks
        for (TaskInstance<PreparationTask> taskInstance : preparationTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            final TaskLogger taskLogger = log.createTaskLogger(taskInstance.getTask(), taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(taskLogger, PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resourceRef.getExpectedType(), processingData);
            taskInstance.getTask().prepare(taskContext, resourceRef);
        }

        // loading task
        LOG.debug("{}::load", loadingTask.getTaskNamespace());
        final TaskLogger loadTaskLogger = log.createTaskLogger(loadingTask.getTask(), loadingTask.getTaskNamespace());
        TaskContext loadContext = context.createTaskContext(loadTaskLogger, PIPELINE_EXECUTION, loadingTask.getTaskNamespace(), resourceRef.getExpectedType(), processingData);
        ExternalResource resource = loadingTask.getTask().load(loadContext, resourceRef, factory);

        // processing tasks
        for (TaskInstance<ProcessingTask> taskInstance : processingTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            final TaskLogger taskLogger = log.createTaskLogger(taskInstance.getTask(), taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(taskLogger, PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resource.getType(), processingData);
            taskInstance.getTask().process(taskContext, resource);
        }

        log.setTime(String.format("%,d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
        return resource;
    }

    public void validatePipeline() {
        if (loadingTask == null) {
            throw new AppIntegrationException("A LoadingTask must be added to the Pipeline before!");
        }
    }


}
