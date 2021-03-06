package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.Reader;

@Component
public class RegexReplaceFilter implements ProcessingTask {

    @Override
    public void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource) {
        final String regex = taskContext.getValue("regex", String.class);
        final String replacement = taskContext.getValue("replacement", String.class);

        if (StringUtils.isNotBlank(regex) && (replacement != null)) {
            resource.setContent(new SearchReplaceReader(resource.getContentAsReader(), regex, replacement));
        }
    }


    private static class SearchReplaceReader extends LineFilterReader {

        private final String regex;

        private final String replacement;

        public SearchReplaceReader(Reader input, String regex, String replacement) {
            super(input);
            this.regex = regex;
            this.replacement = replacement;
        }

        @Override
        protected String filterLine(String line) {
            return line.replaceAll(regex, replacement);
        }
    }
}
