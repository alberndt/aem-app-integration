package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.engine.ProcessingPipelineFactory;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.builder.YamlPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.definition.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.context.GlobalContext;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for processing pipelines, which loads pipeline-definitions as Yaml-files from the classpath (= system resource).
 *
 * @see YamlPipelineBuilder
 */
public class SystemResourcePipelineFactory<C extends GlobalContext> implements ProcessingPipelineFactory<C> {

    @Nonnull
    private final String rootPath;

    @Nonnull
    private final TaskFactory taskFactory;

    private final Map<URI, PipelineDefinition> pipelineDefinitionMap = new HashMap<>();

    public SystemResourcePipelineFactory(@Nonnull final TaskFactory taskFactory, @Nonnull final String rootPath) {
        this.taskFactory = taskFactory;
        this.rootPath = StringUtils.appendIfMissing(rootPath, "/");
    }

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param context Global processing context
     * @param name    Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Override
    @Nonnull
    public ProcessingPipeline createProcessingPipeline(@Nonnull final C context, @Nonnull final String name) {
        final URL pipelineDefUrl = ClassLoader.getSystemResource(rootPath + StringUtils.appendIfMissing(name, ".yaml"));
        final ResourceLogger pipelineLog = context.getIntegrationLog().createResourceLogger(ExternalResourceRef.create(pipelineDefUrl.toExternalForm(), ExternalResourceType.ANY));
        return YamlPipelineBuilder.build(context, taskFactory, pipelineLog, getPipelineDefinition(pipelineLog, pipelineDefUrl, name));
    }

    @Nonnull
    private PipelineDefinition getPipelineDefinition(@Nonnull ResourceLogger pipelineLog, @Nonnull URL pipelineDefUrl, @Nonnull String name) {

        final URI pipelineDefUri;
        try {
            pipelineDefUri = pipelineDefUrl.toURI().normalize();
        } catch (URISyntaxException e) {
            pipelineLog.addWarning("Cannot load pipeline " + name + ", due to " + e.getMessage());
            pipelineLog.setStatus(LogStatus.FAILED);
            throw new AppIntegrationException("Cannot load pipeline " + name + ", due to " + e.getMessage(), e);
        }

        final PipelineDefinition definition;
        if (pipelineDefinitionMap.containsKey(pipelineDefUri)) {
            definition = pipelineDefinitionMap.get(pipelineDefUri);
            pipelineLog.setLoadStatus((definition != null) ? "Cached" : "Cached - Not found");
        } else {
            PipelineDefinition loadedDef = null;
            try {
                loadedDef = YamlPipelineBuilder.parsePipelineDefinitionYaml(pipelineDefUrl.openStream());
            } catch (IOException e) {
                pipelineLog.setSummary(LogStatus.ERROR, "Failed to parse %s due to %s!", e.getMessage());
            } finally {
                definition = loadedDef;
                pipelineDefinitionMap.put(pipelineDefUri, loadedDef);
                pipelineLog.setLoadStatus((definition != null) ? "Found" : "Not found");
            }
        }

        if (definition == null) {
            throw new AppIntegrationException(String.format("ProcessingPipeline %s not found!", name));
        }
        return definition;
    }

}
