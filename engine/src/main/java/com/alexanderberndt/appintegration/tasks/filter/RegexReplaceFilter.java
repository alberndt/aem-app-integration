package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;

public class RegexReplaceFilter implements ProcessingTask {

    @Override
    public String getName() {
        return "search-replace-filter";
    }

    @Override
    public void process(TaskContext context, ExternalResource resource) throws IOException {
        final String regex = context.getValue("regex", String.class);
        final String replacement = context.getValue("replacement", String.class);

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
