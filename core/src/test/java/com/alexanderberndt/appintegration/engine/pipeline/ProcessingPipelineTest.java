package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.engine.converter.StreamToReaderConverter;
import com.alexanderberndt.appintegration.engine.filter.TextSnippetExtractor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingPipelineTest {

    @Test
    void filter() throws IOException {

        InputStream input1 = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");

        StreamToReaderConverter streamToReaderConverter = new StreamToReaderConverter();

        Reader input2 = streamToReaderConverter.filter(null, input1);

        TextSnippetExtractor textSnippetExtractor = new TextSnippetExtractor();

        Reader input3 = textSnippetExtractor.filter(null, input2);

        StringWriter result = new StringWriter();

        IOUtils.copy(input3, result);

        System.out.println("----");
        System.out.print(result);
        System.out.println("----");

        assertNotNull(result);





    }
}