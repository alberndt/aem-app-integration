package com.alexanderberndt.appintegration.pipeline.builder.yaml;

import com.alexanderberndt.appintegration.pipeline.builder.PipelineDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class YamlPipelineParser {

    private static final ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());

    private YamlPipelineParser() {
    }

    public static PipelineDefinition parsePipelineDefinitionYaml(InputStream inputStream) throws IOException {
        return yamlObjectMapper.readerFor(YamlPipelineDefinition.class).readValue(inputStream);
    }

}
