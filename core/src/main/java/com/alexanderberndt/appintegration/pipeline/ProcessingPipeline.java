package com.alexanderberndt.appintegration.pipeline;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.List;

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


    public static BasicPipelineBuilder createPipelineInstance(@Nonnull GlobalContext context) {
        return new BasicPipelineBuilder(context);
    }

    public static PipelineBuilder createPipelineInstance(@Nonnull GlobalContext context, @Nonnull TaskFactory taskFactory) {
        return new PipelineBuilder(context, taskFactory);
    }


    public ExternalResource loadAndProcessResourceRef(GlobalContext context, ExternalResourceRef resourceRef) {

        if (loadingTask == null) {
            throw new AppIntegrationException("A LoadingTask must be added to the Pipeline before!");
        }

        // preparation tasks
        for (TaskInstance<PreparationTask> taskInstance : preparationTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resourceRef.getExpectedType());
            taskInstance.getTask().prepare(taskContext, resourceRef);
        }

        // loading task
        LOG.debug("{}::load", loadingTask.getTaskNamespace());
        TaskContext loadContext = context.createTaskContext(PIPELINE_EXECUTION, loadingTask.getTaskNamespace(), resourceRef.getExpectedType());
        ExternalResource resource = loadingTask.getTask().load(loadContext, resourceRef);

        // processing tasks
        for (TaskInstance<ProcessingTask> taskInstance : processingTasks) {
            LOG.debug("{}::prepare", taskInstance.getTaskNamespace());
            TaskContext taskContext = context.createTaskContext(PIPELINE_EXECUTION, taskInstance.getTaskNamespace(), resource.getType());
            taskInstance.getTask().process(taskContext, resource);
        }

        return resource;
    }


}
