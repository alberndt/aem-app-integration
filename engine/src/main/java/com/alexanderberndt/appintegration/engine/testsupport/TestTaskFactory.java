package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import com.alexanderberndt.appintegration.tasks.process.FileSizeValidationTask;
import com.alexanderberndt.appintegration.tasks.process.RegexValidationTask;
import com.alexanderberndt.appintegration.tasks.process.html.ExtractHtmlSnippetTask;

import java.util.Arrays;

public class TestTaskFactory extends TaskFactory {

    public TestTaskFactory() {
        registerPreparationTasks(new PropertiesTask());

        registerProcessingTasks(
                new AddReferencedResourceTask(),
                new FileSizeValidationTask(),
                new RegexValidationTask(),
                new ExtractHtmlSnippetTask());
    }

    public void registerPreparationTasks(PreparationTask... tasks) {
        if (tasks != null) {
            Arrays.stream(tasks).forEach(task -> super.registerPreparationTask(task, null));
        }
    }

    public void registerProcessingTasks(ProcessingTask... tasks) {
        if (tasks != null) {
            Arrays.stream(tasks).forEach(task -> super.registerProcessingTask(task, null));
        }
    }
}
