package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.builder.PipelineDefinition;
import com.alexanderberndt.appintegration.pipeline.builder.ProcessingPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.yaml.YamlPipelineParser;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

@Component(service = AemProcessingPipelineFactory.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = AemProcessingPipelineFactory.Configuration.class, factory = true)
public class AemProcessingPipelineFactory {

    @ObjectClassDefinition(name = "AEM App-Integration - Processing Pipeline Factory")
    @interface Configuration {

        @AttributeDefinition(
                name = "Path",
                description = "Path to .yaml-files with pipeline definitions."
        )
        @Nonnull
        String path();
    }

    private final ProcessingPipelineBuilder builder;

    private final String path;

    @Activate
    public AemProcessingPipelineFactory(@Nonnull Configuration configuration, @Reference TaskFactory taskFactory) {
        this.path = configuration.path();
        this.builder = new ProcessingPipelineBuilder(taskFactory);
    }

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param resolver ResourceResolver to load the yaml-files
     * @param name    Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    public ProcessingPipeline createProcessingPipeline(@Nonnull ResourceResolver resolver, @Nonnull String name) {

        final Resource rootRes = resolver.getResource(this.path);
        if (rootRes == null) {
            throw new AppIntegrationException(String.format("Root-path %s not found! Cannot create processing pipeline!", this.path));
        }

        final String yamlResName = name + ".yaml";
        final Resource yamlRes = rootRes.getChild(yamlResName);
        if (yamlRes == null) {
            throw new AppIntegrationException(String.format("Processing pipeline %s not found in path %s.", yamlResName, this.path));
        }

        final InputStream yamlInputStream = yamlRes.adaptTo(InputStream.class);
        if (yamlInputStream == null) {
            throw new AppIntegrationException("Resource " + yamlRes.getPath() + " is not a file and cannot be parsed");
        }

        try {
            final PipelineDefinition pipelineDefinition = YamlPipelineParser.parsePipelineDefinitionYaml(yamlInputStream);
            return builder.createProcessingPipeline(pipelineDefinition);
        } catch (IOException | RuntimeException e) {
            throw new AppIntegrationException("Cannot load pipeline " + name, e);
        }
    }

}
