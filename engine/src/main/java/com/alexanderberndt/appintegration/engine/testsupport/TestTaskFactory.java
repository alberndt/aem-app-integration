package com.alexanderberndt.appintegration.engine.testsupport;

import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.task.LoadingTask;
import com.alexanderberndt.appintegration.pipeline.task.PreparationTask;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.cache.ReadFromCacheTask;
import com.alexanderberndt.appintegration.tasks.cache.StoreInCacheTask;
import com.alexanderberndt.appintegration.tasks.load.DownloadTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import com.alexanderberndt.appintegration.tasks.process.FileSizeValidationTask;
import com.alexanderberndt.appintegration.tasks.process.RegexValidationTask;
import com.alexanderberndt.appintegration.tasks.process.html.ExtractHtmlSnippetTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TestTaskFactory implements TaskFactory {

    private final Map<String, PreparationTask> preparationTaskMap = new HashMap<>();

    private final Map<String, LoadingTask> loadingTaskMap = new HashMap<>();

    private final Map<String, ProcessingTask> processingTaskMap = new HashMap<>();

    public TestTaskFactory() {
        registerTask(new PropertiesTask());
        registerTask(new DownloadTask());
        registerTask(new AddReferencedResourceTask());
        registerTask(new FileSizeValidationTask());
        registerTask(new RegexValidationTask());
        registerTask(new StoreInCacheTask());
        registerTask(new ReadFromCacheTask());
        registerTask(new ExtractHtmlSnippetTask());
    }

    @Nullable
    @Override
    public PreparationTask getPreparationTask(@Nonnull String name) {
        return preparationTaskMap.get(name);
    }

    @Nullable
    @Override
    public LoadingTask getLoadingTask(@Nonnull String name) {
        return loadingTaskMap.get(name);
    }

    @Nullable
    @Override
    public ProcessingTask getProcessingTask(@Nonnull String name) {
        return processingTaskMap.get(name);
    }

    public void registerTask(PreparationTask task) {
        this.preparationTaskMap.put(getDefaultTaskName(task.getClass()), task);
    }

    public void registerTask(LoadingTask task) {
        this.loadingTaskMap.put(getDefaultTaskName(task.getClass()), task);
    }

    public void registerTask(ProcessingTask task) {
        this.processingTaskMap.put(getDefaultTaskName(task.getClass()), task);
    }
}
