package com.alexanderberndt.appintegration.engine.filter;

import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;
import com.alexanderberndt.appintegration.engine.utils.LineFilterReader;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.util.Map;

public class RegexReplaceFilter implements PipelineFilter<Reader, Reader> {

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
        final Map<String, Object> config = context.getPipelineFilterConfiguration();
        final String regex = (String) config.get("regex");
        final String replacement = (String) config.get("replacement");

        if (StringUtils.isNotBlank(regex) && (replacement != null)) {
            return new SearchReplaceReader(input, regex, replacement);
        } else {
            return input;
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
