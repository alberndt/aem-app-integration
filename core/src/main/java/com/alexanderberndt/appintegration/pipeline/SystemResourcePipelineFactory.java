package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.ProcessingPipelineFactory;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.builder.YamlPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.definition.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SystemResourcePipelineFactory implements ProcessingPipelineFactory {

    @Nonnull
    private final String rootPath;

    @Nonnull
    private final TaskFactory taskFactory;

    private final Map<String, PipelineDefinition> pipelineDefinitionMap = new HashMap<>();

    public SystemResourcePipelineFactory(@Nonnull final TaskFactory taskFactory, @Nonnull final String rootPath) {
        this.taskFactory = taskFactory;
        this.rootPath = StringUtils.appendIfMissing(rootPath, "/");
    }

    @Override
    public ProcessingPipeline createProcessingPipeline(@Nonnull final GlobalContext context, @Nonnull final String name) {
        final PipelineDefinition definition;
        if (pipelineDefinitionMap.containsKey(name)) {
            definition = pipelineDefinitionMap.get(name);
        } else {
            PipelineDefinition loadedDef = null;
            try {
                final InputStream inputStream = ClassLoader.getSystemResourceAsStream(rootPath + StringUtils.appendIfMissing(name, ".yaml"));
                if (inputStream != null) {
                    try {
                        loadedDef = YamlPipelineBuilder.parsePipelineDefinitionYaml(inputStream);
                    } catch (IOException e) {
                        context.addError(e.getMessage());
                    }
                }
            } finally {
                definition = loadedDef;
                pipelineDefinitionMap.put(name, loadedDef);
            }
        }

        if (definition == null) {
            throw new AppIntegrationException(String.format("ProcessingPipeline %s not found!", name));
        }

        return YamlPipelineBuilder.build(context, taskFactory, definition);
    }
}
