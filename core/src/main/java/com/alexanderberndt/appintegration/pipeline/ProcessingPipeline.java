package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.logging.ResourceLog;
import com.alexanderberndt.appintegration.engine.logging.TaskLog;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
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
    public static BasicPipelineBuilder createPipelineInstance(@Nonnull GlobalContext context, @Nonnull ResourceLog pipelineLog) {
        return new BasicPipelineBuilder(context, pipelineLog);
    }

    @Deprecated
    public static PipelineBuilder createPipelineInstance(@Nonnull GlobalContext context, @Nonnull TaskFactory taskFactory, @Nonnull ResourceLog pipelineLog) {
        return new PipelineBuilder(context, taskFactory, pipelineLog);
    }


    public ExternalResource loadAndProcessResourceRef(ExternalResourceRef resourceRef) {

        validatePipeline();

        final ResourceLog log = context.getIntegrationLog().createResourceEntry(resourceRef);
        final Map<String, Object> processingData = new HashMap<>();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // preparation tasks
        for (TaskInstance<PreparationTask> taskInstance : preparationTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            final TaskLog taskLog = log.createTaskEntry(taskInstance.getTask(), taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(taskLog, PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resourceRef.getExpectedType(), processingData);
            taskInstance.getTask().prepare(taskContext, resourceRef);
        }

        // loading task
        LOG.debug("{}::load", loadingTask.getTaskNamespace());
        final TaskLog loadTaskLog = log.createTaskEntry(loadingTask.getTask(), loadingTask.getTaskNamespace());
        TaskContext loadContext = context.createTaskContext(loadTaskLog, PIPELINE_EXECUTION, loadingTask.getTaskNamespace(), resourceRef.getExpectedType(), processingData);
        ExternalResource resource = loadingTask.getTask().load(loadContext, resourceRef);

        // processing tasks
        for (TaskInstance<ProcessingTask> taskInstance : processingTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            final TaskLog taskLog = log.createTaskEntry(taskInstance.getTask(), taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(taskLog, PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resource.getType(), processingData);
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
