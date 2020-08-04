package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;

import java.util.ArrayList;
import java.util.List;

import static com.alexanderberndt.appintegration.pipeline.valuemap.Ranking.PIPELINE_DEFINITION;
import static com.alexanderberndt.appintegration.pipeline.valuemap.Ranking.TASK_DEFAULT;

public class ProcessingPipelineBuilder {

    private final GlobalContext context;

    private final List<TaskInstance<PreparationTask>> preparationTasks = new ArrayList<>();

    private TaskInstance<LoadingTask> loadingTask;

    private final List<TaskInstance<ProcessingTask>> processingTasks = new ArrayList<>();

    private String currentNamespace = null;

    private TaskContext currentTaskContext;


    private ProcessingPipelineBuilder(GlobalContext context) {
        this.context = context;
    }

    public static ProcessingPipelineBuilder createPipelineInstance(GlobalContext context) {
        return new ProcessingPipelineBuilder(context);
    }

    public ProcessingPipelineBuilder addTask(GenericTask task) {
        return addTask(task, task.getName());
    }


    public ProcessingPipelineBuilder addTask(GenericTask task, String taskId) {

        this.currentNamespace = taskId;
        this.currentTaskContext = null;

        // define defaults
        TaskContext taskContext = context.createTaskContext(TASK_DEFAULT, taskId);
        task.declareTaskPropertiesAndDefaults(taskContext);

        int type = ((task instanceof PreparationTask) ? 1 : 0)
                + ((task instanceof LoadingTask) ? 2 : 0)
                + ((task instanceof ProcessingTask) ? 4 : 0);

        switch (type) {
            case 0:
                throw new AppIntegrationException(String.format("Task %s must be either Preparation-, Loading- or Processing-Task!", taskId));
            case 1:
                addPreparationTask((PreparationTask) task, taskId);
                break;
            case 2:
                addLoadingTask((LoadingTask) task, taskId);
                break;
            case 4:
                addProcessingTask((ProcessingTask) task, taskId);
                break;
            case 5:
                if (loadingTask == null) {
                    addPreparationTask((PreparationTask) task, taskId);
                } else {
                    addProcessingTask((ProcessingTask) task, taskId);
                }
                break;
            default:
                throw new AppIntegrationException(String.format("Task %s cannot be Loading- and Preparation- or Processing-Task!", taskId));
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


    public ProcessingPipelineBuilder withTaskParam(String param, Object value) {
        if (currentTaskContext == null) {
            currentTaskContext = context.createTaskContext(PIPELINE_DEFINITION, currentNamespace);
        }
        currentTaskContext.setValue(param, value);
        return this;
    }

    public ProcessingPipeline build() {
        return new ProcessingPipeline(context, preparationTasks, loadingTask, processingTasks);
    }

}
