package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskInstance;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alexanderberndt.appintegration.pipeline.configuration.Ranking.PIPELINE_DEFINITION;
import static com.alexanderberndt.appintegration.pipeline.configuration.Ranking.TASK_DEFAULT;

public class BasicPipelineBuilder {

    private final GlobalContext context;

    private final List<TaskInstance<PreparationTask>> preparationTasks = new ArrayList<>();

    private TaskInstance<LoadingTask> loadingTask;

    private final List<TaskInstance<ProcessingTask>> processingTasks = new ArrayList<>();

    private String currentNamespace = null;

    private TaskContext currentTaskContext;

    public BasicPipelineBuilder(@Nonnull GlobalContext context) {
        this.context = context;
    }

    public BasicPipelineBuilder addTask(@Nonnull GenericTask task) {
        return addTask(task, task.getName());
    }


    public BasicPipelineBuilder addTask(GenericTask task, String uniqueTaskNamespace) {

        this.currentNamespace = uniqueTaskNamespace;
        this.currentTaskContext = null;

        // define defaults
        TaskContext taskContext = context.createTaskContext(TASK_DEFAULT, uniqueTaskNamespace, ExternalResourceType.ANY, Collections.emptyMap());
        task.declareTaskPropertiesAndDefaults(taskContext);

        int type = ((task instanceof PreparationTask) ? 1 : 0)
                + ((task instanceof LoadingTask) ? 2 : 0)
                + ((task instanceof ProcessingTask) ? 4 : 0);

        switch (type) {
            case 0:
                throw new AppIntegrationException(String.format("Task %s must be either Preparation-, Loading- or Processing-Task!", uniqueTaskNamespace));
            case 1:
                addPreparationTask((PreparationTask) task, uniqueTaskNamespace);
                break;
            case 2:
                addLoadingTask((LoadingTask) task, uniqueTaskNamespace);
                break;
            case 4:
                addProcessingTask((ProcessingTask) task, uniqueTaskNamespace);
                break;
            case 5:
                if (loadingTask == null) {
                    addPreparationTask((PreparationTask) task, uniqueTaskNamespace);
                } else {
                    addProcessingTask((ProcessingTask) task, uniqueTaskNamespace);
                }
                break;
            default:
                throw new AppIntegrationException(String.format("Task %s cannot be Loading- and Preparation- or Processing-Task!", uniqueTaskNamespace));
        }

        return this;
    }


    private void addPreparationTask(PreparationTask task, String taskId) {
        if (loadingTask == null) {
            preparationTasks.add(new TaskInstance<>(task, taskId));
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. After adding a LoadingTask, only ProcessingTasks can be added.", taskId));
        }
    }

    private void addLoadingTask(LoadingTask task, String taskId) {
        if (loadingTask == null) {
            loadingTask = new TaskInstance<>(task, taskId);
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. Only one LoadingTask can be added.", taskId));
        }
    }

    private void addProcessingTask(ProcessingTask task, String taskId) {
        if (loadingTask != null) {
            processingTasks.add(new TaskInstance<>(task, taskId));
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added. A LoadingTask must be added before any ProcessingTasks.", taskId));
        }
    }


    public BasicPipelineBuilder withTaskParam(String param, Object value) {
        if (currentTaskContext == null) {
            currentTaskContext = context.createTaskContext(PIPELINE_DEFINITION, currentNamespace, ExternalResourceType.ANY, Collections.emptyMap());
        }
        currentTaskContext.setValue(param, value);
        return this;
    }

    public ProcessingPipeline build() {
        return new ProcessingPipeline(context, preparationTasks, loadingTask, processingTasks);
    }

}
