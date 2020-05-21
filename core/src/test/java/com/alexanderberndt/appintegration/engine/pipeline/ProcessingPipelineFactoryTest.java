package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.engine.filter.TextSnippetExtractor;
import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineDefinition;
import com.alexanderberndt.appintegration.engine.validators.FileSizeValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProcessingPipelineFactoryTest {

    @Test
    void testPipeline1() throws IOException {

        InputStream pipelineInputStream = ClassLoader.getSystemResourceAsStream("simple-app1/local/integration-pipeline.json");
        ObjectMapper objectMapper = new ObjectMapper();
        PipelineDefinition pipelineDefinition = objectMapper.readValue(pipelineInputStream, PipelineDefinition.class);
        assertNotNull(pipelineDefinition);

        ProcessingPipelineFactory factory = new ProcessingPipelineFactory();
        factory.register(new TextSnippetExtractor());
        factory.register(new FileSizeValidator());

        ProcessingPipeline pipeline = factory.createPipeline(pipelineDefinition);

        InputStream input = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
        Reader filteredInput = pipeline.filter(null, input);
        assertNotNull(filteredInput);

        StringWriter result = new StringWriter();
        IOUtils.copy(filteredInput, result);

        System.out.println("----");
        System.out.print(result.toString());
        System.out.println("----");

        assertNotNull(result);


    }

}