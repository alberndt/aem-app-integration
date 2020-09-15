package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.builder.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.builder.ProcessingPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.yaml.YamlPipelineParser;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for processing pipelines, which loads pipeline-definitions as Yaml-files from the classpath (= system resource).
 *
 * @see YamlPipelineParser
 */
public class SystemResourcePipelineFactory {

    @Nonnull
    private final String rootPath;

    @Nonnull
    private final ProcessingPipelineBuilder pipelineBuilder;

    private final Map<String, ProcessingPipeline> pipelineDefinitionMap = new HashMap<>();

    public SystemResourcePipelineFactory(@Nonnull final TaskFactory taskFactory, @Nonnull final String rootPath) {
        this.rootPath = StringUtils.appendIfMissing(rootPath, "/");
        this.pipelineBuilder = new ProcessingPipelineBuilder(taskFactory);
    }

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param name Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    public ProcessingPipeline createProcessingPipeline(@Nonnull final String name) throws AppIntegrationException {

        // first try a cache-hit
        if (pipelineDefinitionMap.containsKey(name)) {
            return pipelineDefinitionMap.get(name);
        }

        final String resourcePath = rootPath + StringUtils.appendIfMissing(name, ".yaml");
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourcePath);
        if (inputStream != null) {
            try {

                final PipelineDefinition pipelineDef = YamlPipelineParser.parsePipelineDefinitionYaml(inputStream);
                ProcessingPipeline pipeline = pipelineBuilder.createProcessingPipeline(pipelineDef);
                pipelineDefinitionMap.put(name, pipeline);
                return pipeline;

            } catch (IOException e) {
                throw new AppIntegrationException("Failed to load processing pipeline " + resourcePath, e);
            }
        } else {
            throw new AppIntegrationException("Processing pipeline " + name + " was not found! Expected path in classpath is " + resourcePath);
        }
    }
}
