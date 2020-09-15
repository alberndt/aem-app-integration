package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.TaskWrapper;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ProcessingPipelineBuilder {

    private final TaskFactory taskFactory;

    public ProcessingPipelineBuilder(@Nonnull TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public ProcessingPipeline createProcessingPipeline(@Nonnull PipelineDefinition pipelineDef) {

        final Set<String> taskNameSet = new HashSet<>();

        final List<TaskWrapper<PreparationTask>> preparationTasks = createTaskInstanceList(pipelineDef.getPreparationTasks(), taskFactory::getPreparationTask, taskNameSet);
        final List<TaskWrapper<LoadingTask>> downloadTasks = createTaskInstanceList(pipelineDef.getLoaderTasks(), taskFactory::getLoadingTask, taskNameSet);
        final List<TaskWrapper<ProcessingTask>> processingTasks = createTaskInstanceList(pipelineDef.getProcessingTasks(), taskFactory::getProcessingTask, taskNameSet);

        if (downloadTasks.isEmpty()) {
            throw new AppIntegrationException("Cannot create pipeline, as it doesn't contain a loading task");
        }
        if (downloadTasks.size() > 1) {
            throw new AppIntegrationException("Cannot create pipeline, as it contains " + downloadTasks.size() + " loading tasks. But it MUST be exactly one!");
        }

        return new ProcessingPipeline(preparationTasks, downloadTasks.get(0), processingTasks);
    }

    @Nonnull
    private <T> List<TaskWrapper<T>> createTaskInstanceList(
            @Nullable Map<String, TaskDefinition> taskDefMap,
            @Nonnull Function<String, T> taskFactoryMethod,
            @Nonnull Set<String> taskNameSet) {

        final List<TaskWrapper<T>> taskList = new ArrayList<>();
        if ((taskDefMap != null) && (!taskDefMap.isEmpty())) {
            for (Map.Entry<String, TaskDefinition> taskDefEntry : taskDefMap.entrySet()) {
                final String taskId = taskDefEntry.getKey();
                final TaskDefinition taskDef = taskDefEntry.getValue();
                taskList.add(createTaskWrapper(taskId, taskDef, taskNameSet, taskFactoryMethod));
            }
        }
        return taskList;
    }

    @Nonnull
    private <T> TaskWrapper<T> createTaskWrapper(@Nonnull String taskId, @Nullable TaskDefinition taskDef, @Nonnull Set<String> taskNameSet, @Nonnull Function<String, T> taskFactoryMethod) {
        final String taskName = StringUtils.defaultIfBlank((taskDef != null) ? taskDef.getName() : null, taskId);

        final T task = taskFactoryMethod.apply(taskName);
        if (task == null) {
            throw new AppIntegrationException("task " + taskName + " is not found by " + taskFactoryMethod.toString() + ". Cannot create pipeline!");
        }

        if (!taskNameSet.add(taskId)) {
            throw new AppIntegrationException("Cannot create pipeline, as task-id " + taskId + " is used twice! It must be unique id for the entire pipeline!");
        }

        final DataMap configuration = (taskDef != null) ? taskDef.getConfiguration() : null;
        return new TaskWrapper<>(taskId, taskName, task, configuration);
    }

}
