package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.api.task.GenericTask;
import com.alexanderberndt.appintegration.api.task.LoadingTask;
import com.alexanderberndt.appintegration.api.task.PreparationTask;
import com.alexanderberndt.appintegration.api.task.ProcessingTask;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.ValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessingPipeline implements LoadingTask {

    private final TaskFactory taskFactory;

    private final ValueMap globalValueMap;

    private final List<TaskDefinition<PreparationTask>> preparationTasks = new ArrayList<>();

    private TaskDefinition<LoadingTask> loadingTask;

    private final List<TaskDefinition<ProcessingTask>> processingTasks = new ArrayList<>();


    public ProcessingPipeline(TaskFactory taskFactory, Map<String, Object> globalProperties) {
        this.taskFactory = taskFactory;
        this.globalValueMap = new ValueMap(globalProperties, false);
    }

    public void addTask(String taskName, Map<String, Object> taskProperties) {
        GenericTask task = taskFactory.getTask(taskName);
        if (task == null) {
            throw new AppIntegrationException(String.format("Task %s is undefined.", taskName));
        }

        final ValueMap taskProps = new ValueMap(globalValueMap, taskName, taskProperties, false);
        boolean isAdded = false;

        if (task instanceof PreparationTask) {
            if (loadingTask == null) {
                preparationTasks.add(new TaskDefinition<>(taskName, (PreparationTask) task, taskProps));
                isAdded = true;
            } else {
                throw new AppIntegrationException(String.format("Task %s cannot be added anymore. After adding a LoadingTask, only ProcessingTasks can be added.", taskName));
            }
        }

        if (task instanceof LoadingTask) {
            if (loadingTask == null) {
                loadingTask = new TaskDefinition<>(taskName, (LoadingTask) task, taskProps);
                isAdded = true;
            } else {
                throw new AppIntegrationException(String.format("Task %s cannot be added anymore. Only one LoadingTask can be added.", taskName));
            }
        }

        if (task instanceof ProcessingTask) {
            if (loadingTask != null) {
                processingTasks.add(new TaskDefinition<>(taskName, (ProcessingTask) task, taskProps));
                isAdded = true;
            } else {
                throw new AppIntegrationException(String.format("Task %s cannot be added. A LoadingTask must be added before any ProcessingTasks.", taskName));
            }
        }

        if (!isAdded) {
            throw new AppIntegrationException(String.format("Task %s must be either Preparation-, Loading- or Processing-Task!", taskName));
        }
    }


    @Override
    public String getName() {
        return "processing-pipeline";
    }

    @Override
    public ExternalResource load(ProcessingContext context, ExternalResourceRef resourceRef) {

        if (loadingTask == null) {
            throw new AppIntegrationException("A LoadingTask must be added to the Pipeline before!");
        }

        // preparation tasks
        prepareInternal(context, resourceRef);
        // loading task
        final ExternalResource resource = loadInternal(context, resourceRef);
        // processing tasks
        processInternal(context, resource);

        return resource;
    }

    private void prepareInternal(ProcessingContext context, ExternalResourceRef resourceRef) {
        for (TaskDefinition<PreparationTask> taskDef : preparationTasks) {
            PreparationTask task = taskDef.getTask();
            ProcessingContext taskContext = taskDef.createChildContext(context);
            task.prepare(taskContext, resourceRef);
        }
    }

    private ExternalResource loadInternal(ProcessingContext context, ExternalResourceRef resourceRef) {
        ExternalResource resource;
        LoadingTask task = loadingTask.getTask();
        ProcessingContext taskContext = loadingTask.createChildContext(context);

        resource = task.load(taskContext, resourceRef);
        return resource;
    }

    private void processInternal(ProcessingContext context, ExternalResource resource) {
        for (TaskDefinition<ProcessingTask> taskDef : processingTasks) {
            ProcessingTask task = taskDef.getTask();
            ProcessingContext taskContext = taskDef.createChildContext(context);

            task.process(taskContext, resource);
        }
    }


    private static class TaskDefinition<T> {

        private final String taskName;

        private final T task;

        private final ValueMap taskProperties;

        public TaskDefinition(String taskName, T task, ValueMap taskProperties) {
            this.taskName = taskName;
            this.task = task;
            this.taskProperties = taskProperties;
        }

        public String getTaskName() {
            return taskName;
        }

        public T getTask() {
            return task;
        }

        public ValueMap getTaskProperties() {
            return taskProperties;
        }

        public ProcessingContext createChildContext(ProcessingContext parentContext) {
            return parentContext.createChildContext(taskName, "Task " + taskName, taskProperties);
        }

    }

}
