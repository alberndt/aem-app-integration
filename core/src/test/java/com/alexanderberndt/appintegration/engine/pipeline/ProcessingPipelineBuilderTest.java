package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.tasks.converter.StreamToReaderConverter;
import com.alexanderberndt.appintegration.tasks.filter.RegexReplaceFilter;
import com.alexanderberndt.appintegration.tasks.filter.SearchReplaceFilter;
import com.alexanderberndt.appintegration.tasks.filter.TextSnippetExtractor;
import com.alexanderberndt.appintegration.pipeline.ProcessingContext;
import com.alexanderberndt.appintegration.tasks.validators.FileSizeValidator;
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

        // ToDo: Re-Factor
//        InputStream input = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
//        assertNotNull(input);
//
//        ProcessingContext context = new ProcessingContext();
//
//
//        ProcessingPipelineBuilder<InputStream> builder = new ProcessingPipelineBuilder<>(input);
//        builder.appendFilter(null, new FileSizeValidator());
//        builder.appendFilter(null, new FileSizeValidator());
//        builder.appendFilter(null, new StreamToReaderConverter());
//        builder.appendFilter(null, new TextSnippetExtractor());
//        builder.appendFilter(null, new SearchReplaceFilter());
//
//
//        Map<String, Object> config = new HashMap<>();
//        config.put("regex", "World");
//        config.put("replacement", "Earth");
//        context.setPipelineFilterConfiguration(config);
//
//
//        builder.appendFilter(context, new RegexReplaceFilter());
//
//        StringWriter output = new StringWriter();
//
//        IOUtils.copy((Reader) builder.getCurrentPipelineOutput(), output);
//
//        System.out.println("---------------");
//        System.out.print(output.toString());
//        System.out.println("---------------");

    }

}