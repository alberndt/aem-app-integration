package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.tasks.CoreTaskFactory;
import com.alexanderberndt.appintegration.tasks.converter.StreamToReaderConverter;
import com.alexanderberndt.appintegration.tasks.filter.TextSnippetExtractor;
import com.alexanderberndt.appintegration.utils.ValueMap;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingPipelineTest {

    @Test
    void addTaskWithSuccess() {

    }

    @Test
    void simpleProcessingPipeline() {

        ValueMap emptyProperties = new ValueMap(false);
        ProcessingContext context = new ProcessingContext(emptyProperties);
        ProcessingPipeline pipeline = new ProcessingPipeline(new CoreTaskFactory(), emptyProperties);

        pipeline.addTask("properties", Collections.singletonMap("random-input.length", 2000));


fail("Implete");

//                InputStream input1 = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
//
//        StreamToReaderConverter streamToReaderConverter = new StreamToReaderConverter();
//
//        Reader input2 = streamToReaderConverter.filter(null, input1);
//
//        TextSnippetExtractor textSnippetExtractor = new TextSnippetExtractor();
//
//        Reader input3 = textSnippetExtractor.filter(null, input2);
//
//        StringWriter result = new StringWriter();
//
//        IOUtils.copy(input3, result);
//
//        System.out.println("----");
//        System.out.print(result);
//        System.out.println("----");
//
//        assertNotNull(result);

    }

    @Test
    void load() {
    }


}