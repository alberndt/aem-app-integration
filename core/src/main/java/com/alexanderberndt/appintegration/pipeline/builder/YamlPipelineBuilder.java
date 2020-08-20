package com.alexanderberndt.appintegration.pipeline.builder;

import com.alexanderberndt.appintegration.engine.logging.ResourceLog;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.builder.definition.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.builder.definition.TaskDefinition;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class YamlPipelineBuilder {

    private static final ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());

    private YamlPipelineBuilder() {
    }

    public static PipelineDefinition parsePipelineDefinitionYaml(InputStream inputStream) throws IOException {
        return yamlObjectMapper.readerFor(PipelineDefinition.class).readValue(inputStream);
    }

    public static ProcessingPipeline build(
            @Nonnull GlobalContext context,
            @Nonnull TaskFactory taskFactory,
            @Nonnull ResourceLog pipelineLog,
            @Nonnull PipelineDefinition pipelineDef) {

        final PipelineBuilder builder = new PipelineBuilder(context, taskFactory, pipelineLog);

        for (Map.Entry<String, TaskDefinition> taskEntry : pipelineDef.entrySet()) {

            final String taskId = taskEntry.getKey();
            final TaskDefinition taskDef = (taskEntry.getValue() != null) ? taskEntry.getValue() : new TaskDefinition();

            builder.addTask(StringUtils.defaultIfBlank(taskDef.getName(), taskId), taskId);


            taskDef.getFullConfiguration()
                    .forEach((resType, configMap) -> configMap.forEach((key, value) -> {
                        try {
                            builder.withTaskParam(key, value);
                        } catch (Exception e) {
                            context.addWarning(e.getMessage());
                        }
                    }));
        }

        return builder.build();
    }
}
