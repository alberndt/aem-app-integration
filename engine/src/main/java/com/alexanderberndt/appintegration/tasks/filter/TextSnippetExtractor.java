package com.alexanderberndt.appintegration.tasks.filter;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.context.TaskContext;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import com.alexanderberndt.appintegration.tasks.utils.LineFilterReader;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.Reader;

@Component
public class TextSnippetExtractor implements ProcessingTask {

    @Override
    public String getName() {
        return "text-snippet-extractor";
    }

    @Override
    public void process(TaskContext context, ExternalResource resource) throws IOException {
        resource.setContent(new TextSnippetExtractingReader(resource.getContentAsReader()));
        // ToDo: Implement error handling
    }

    private static class TextSnippetExtractingReader extends LineFilterReader {

        private static final String START_MARKER = "--- start ---";

        private static final String END_MARKER = "--- end ---";

        private boolean isSnippet = false;

        public TextSnippetExtractingReader(Reader input) {
            super(input);
        }

        @Override
        protected String filterLine(String line) {
            String curLine = line;
            if (!isSnippet) {
                // look for start-marker
                int pos = curLine.indexOf(START_MARKER);
                if (pos >= 0) {
                    isSnippet = true;
                    curLine = curLine.substring(pos + START_MARKER.length());
                    if (curLine.trim().length() == 0) {
                        // ignore start-line, if rest of line is empty
                        return null;
                    }
                }
            }

            if (isSnippet) {
                // look for end-marker
                int pos = curLine.indexOf(END_MARKER);
                if (pos >= 0) {
                    isSnippet = false;
                    curLine = curLine.substring(0, pos);
                    if (curLine.trim().length() == 0) {
                        // ignore end-line, if rest of line is empty
                        return null;
                    }
                }

                return curLine;
            }

            return null;
        }
    }
}
