package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;

import java.io.IOException;
import java.io.Reader;

public class SearchReplaceFilter implements ProcessingTask {

    @Override
    public String getName() {
        return "search-replace-filter";
    }

    @Override
    public void process(TaskContext context, ExternalResource resource) {
        try {
            resource.setReader(new SearchReplaceReader(resource.getReader()));
        } catch (IOException e) {
            e.printStackTrace();
            context.addError(e.getMessage());
            // ToDo: Implement error handling
        }
    }

    private static class SearchReplaceReader extends LineFilterReader {

        public SearchReplaceReader(Reader input) {
            super(input);
        }

        @Override
        protected String filterLine(String line) {
            return line.replace("Alex", "Berndt");
        }
    }
}
