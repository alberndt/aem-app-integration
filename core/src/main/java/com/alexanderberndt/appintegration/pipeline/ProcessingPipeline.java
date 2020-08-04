package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Processing Instance.
 */
public class ProcessingPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GlobalContext context;

    private final List<TaskContext<PreparationTask>> preparationTasks;

    private final TaskContext<LoadingTask> loadingTask;

    private final List<TaskContext<ProcessingTask>> processingTasks;

    protected ProcessingPipeline(GlobalContext context,
                                 List<TaskContext<PreparationTask>> preparationTasks,
                                 TaskContext<LoadingTask> loadingTask,
                                 List<TaskContext<ProcessingTask>> processingTasks) {
        this.context = context;
        this.preparationTasks = preparationTasks;
        this.loadingTask = loadingTask;
        this.processingTasks = processingTasks;
    }

    public ExternalResource loadAndProcessResourceRef(GlobalContext context, ExternalResourceRef resourceRef) {

        if (loadingTask == null) {
            throw new AppIntegrationException("A LoadingTask must be added to the Pipeline before!");
        }

        // preparation tasks
        for (TaskContext<PreparationTask> taskContext : preparationTasks) {
            taskContext.getTask().prepare(taskContext, resourceRef);
        }

        // loading task
        ExternalResource resource = loadingTask.getTask().load(loadingTask, resourceRef);

        // processing tasks
        for (TaskContext<ProcessingTask> taskContext : processingTasks) {
            taskContext.getTask().process(taskContext, resource);
        }

        return resource;
    }

}
