package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import com.alexanderberndt.appintegration.tasks.process.FileSizeValidationTask;
import com.alexanderberndt.appintegration.tasks.process.RegexValidationTask;
import com.alexanderberndt.appintegration.tasks.process.html.ExtractHtmlSnippetTask;

public class TestTaskFactory extends TaskFactory {

    public TestTaskFactory() {
        register(new PropertiesTask());
        register(new AddReferencedResourceTask());
        register(new FileSizeValidationTask());
        register(new RegexValidationTask());
        register(new ExtractHtmlSnippetTask());
    }

    public void register(PreparationTask task) {
        super.register(task, null);
    }

    public void register(ProcessingTask task) {
        super.register(task, null);
    }

}
