package com.alexanderberndt.appintegration.engine.filter;

import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;
import com.alexanderberndt.appintegration.engine.utils.LineFilterReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class TextSnippetExtractor implements PipelineFilter<Reader, Reader> {

    @Override
    public String getName() {
        return "text-snippet-extractor";
    }

    @Override
    public Class<Reader> getInputType() {
        return Reader.class;
    }

    @Override
    public Class<Reader> getOutputType() {
        return Reader.class;
    }

    @Override
    public Reader filter(ProcessingContext context, Reader input) {
        return new TextSnippetExtractingReader(input);
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
