package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.api.ContextProvider;
import com.alexanderberndt.appintegration.engine.converter.StreamToReaderConverter;
import com.alexanderberndt.appintegration.engine.filter.RegexReplaceFilter;
import com.alexanderberndt.appintegration.engine.filter.SearchReplaceFilter;
import com.alexanderberndt.appintegration.engine.filter.TextSnippetExtractor;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;
import com.alexanderberndt.appintegration.engine.validators.FileSizeValidator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingPipelineBuilderTest {

    @Test
    void appendFilter() throws IOException {
        InputStream input = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
        assertNotNull(input);

        ProcessingContext context = new ProcessingContext();


        ProcessingPipelineBuilder<InputStream> builder = new ProcessingPipelineBuilder<>(input);
        builder.appendFilter(null, new FileSizeValidator());
        builder.appendFilter(null, new FileSizeValidator());
        builder.appendFilter(null, new StreamToReaderConverter());
        builder.appendFilter(null, new TextSnippetExtractor());
        builder.appendFilter(null, new SearchReplaceFilter());


        Map<String, Object> config = new HashMap<>();
        config.put("regex", "World");
        config.put("replacement", "Earth");
        context.setPipelineFilterConfiguration(config);


        builder.appendFilter(context, new RegexReplaceFilter());

        StringWriter output = new StringWriter();

        IOUtils.copy((Reader) builder.getCurrentPipelineOutput(), output);

        System.out.println("---------------");
        System.out.print(output.toString());
        System.out.println("---------------");

    }

}