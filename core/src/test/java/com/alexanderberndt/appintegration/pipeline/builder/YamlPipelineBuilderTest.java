package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.core.CoreTestGlobalContext;
import com.alexanderberndt.appintegration.engine.loader.SystemResourceLoader;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.builder.definition.PipelineDefinition;
import com.alexanderberndt.appintegration.tasks.CoreTaskFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class YamlPipelineBuilderTest {

    @Test
    void build() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("local/pipelines/simple-pipeline1.yaml");
        assertNotNull(inputStream);

        PipelineDefinition pipelineDef = YamlPipelineBuilder.parsePipelineDefinitionYaml(inputStream);
        assertNotNull(pipelineDef);

        CoreTestGlobalContext context = new CoreTestGlobalContext(new SystemResourceLoader());
        CoreTaskFactory taskFactory = new CoreTaskFactory();
        ProcessingPipeline pipeline = YamlPipelineBuilder.build(context, taskFactory, pipelineDef);
        assertNotNull(pipeline);

    }
}