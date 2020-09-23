package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.Reader;

@Component
public class SearchReplaceFilter implements ProcessingTask {

    @Override
    public void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource)  {
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
