package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.Reader;

@Component
public class SearchReplaceFilter implements ProcessingTask {

    @Override
    public String getName() {
        return "search-replace-filter";
    }

    @Override
    public void process(TaskContext context, ExternalResource resource) throws IOException {
        resource.setContent(new SearchReplaceReader(resource.getContentAsReader()));
        // ToDo: Implement error handling
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
