package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.GenericTask;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.pipeline.valuemap.Ranking;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueException;

import java.util.ArrayList;
import java.util.List;

public class ProcessingPipelineBuilder {

    private final GlobalContext context;

    private final List<TaskContext<PreparationTask>> preparationTasks = new ArrayList<>();

    private TaskContext<LoadingTask> loadingTask;

    private final List<TaskContext<ProcessingTask>> processingTasks = new ArrayList<>();

    private String currentNamespace = null;

    private TaskContext<?> lastAddedTaskContext;


    private ProcessingPipelineBuilder(GlobalContext context) {
        this.context = context;
    }

    public static ProcessingPipelineBuilder createPipelineInstance(GlobalContext context) {
        return new ProcessingPipelineBuilder(context);
    }

    public ProcessingPipelineBuilder addTask(GenericTask<?> task) {
        return addTask(task, task.getName());
    }


    public ProcessingPipelineBuilder addTask(GenericTask<?> task, String taskId) {

        currentNamespace = taskId;

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
            TaskContext<PreparationTask> taskContext = context.createTaskContext(task, taskId);
            taskContext.declareTaskPropertiesAndDefaults();
            preparationTasks.add(taskContext);
            lastAddedTaskContext = taskContext;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. After adding a LoadingTask, only ProcessingTasks can be added.", taskId));
        }
    }

    private void addLoadingTask(LoadingTask task, String taskId) {
        if (loadingTask == null) {
            loadingTask = context.createTaskContext(task, taskId);
            loadingTask.declareTaskPropertiesAndDefaults();
            lastAddedTaskContext = loadingTask;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added anymore. Only one LoadingTask can be added.", taskId));
        }
    }

    private void addProcessingTask(ProcessingTask task, String taskId) {
        if (loadingTask != null) {
            TaskContext<ProcessingTask> taskContext = context.createTaskContext(task, taskId);
            taskContext.declareTaskPropertiesAndDefaults();
            processingTasks.add(taskContext);
            lastAddedTaskContext = taskContext;
        } else {
            throw new AppIntegrationException(String.format("Task %s cannot be added. A LoadingTask must be added before any ProcessingTasks.", taskId));
        }
    }


    public ProcessingPipelineBuilder withTaskParam(String param, Object value) {
        try {
            context.getProcessingParams().setValue(currentNamespace, param, Ranking.PIPELINE_DEFINITION, value);
        } catch (ValueException e) {
            if (lastAddedTaskContext != null) {
                lastAddedTaskContext.addWarning(e.getMessage());
            } else {
                context.addWarning(e.getMessage());
            }
        }
        return this;
    }

    public ProcessingPipeline build() {
        return new ProcessingPipeline(context, preparationTasks, loadingTask, processingTasks);
    }

}
