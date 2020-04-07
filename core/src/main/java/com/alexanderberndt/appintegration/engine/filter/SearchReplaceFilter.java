package com.alexanderberndt.appintegration.engine.filter;

import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;
import com.alexanderberndt.appintegration.engine.utils.LineFilterReader;

import java.io.Reader;

public class SearchReplaceFilter implements PipelineFilter<Reader, Reader> {

    @Override
    public String getName() {
        return "search-replace-filter";
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
        return new SearchReplaceReader(input);
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
