package com.alexanderberndt.appintegration.engine.pipeline;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class ProcessingPipelineBuilderTest {

    @Test
    void appendFilter() throws IOException {

        // ToDo: Re-Factor
//        InputStream input = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
//        assertNotNull(input);
//
//        TaskContext context = new TaskContext();
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