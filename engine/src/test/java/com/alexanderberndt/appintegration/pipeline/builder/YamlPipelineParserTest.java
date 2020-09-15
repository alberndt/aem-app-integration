package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.engine.testsupport.TestTaskFactory;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.yaml.YamlPipelineParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class YamlPipelineParserTest {

    @Test
    void build() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("local/pipelines/simple-pipeline1.yaml");
        assertNotNull(inputStream);

        PipelineDefinition pipelineDef = YamlPipelineParser.parsePipelineDefinitionYaml(inputStream);
        assertNotNull(pipelineDef);

        ProcessingPipelineBuilder builder = new ProcessingPipelineBuilder(new TestTaskFactory());
        ProcessingPipeline pipeline = builder.createProcessingPipeline(pipelineDef);
        assertNotNull(pipeline);
    }
}