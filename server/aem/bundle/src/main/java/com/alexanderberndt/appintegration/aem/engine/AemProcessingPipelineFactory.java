package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.engine.ProcessingPipelineFactory;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskFactory;
import com.alexanderberndt.appintegration.pipeline.builder.YamlPipelineBuilder;
import com.alexanderberndt.appintegration.pipeline.builder.definition.PipelineDefinition;
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
public class AemProcessingPipelineFactory implements ProcessingPipelineFactory<AemGlobalContext> {

    @ObjectClassDefinition(name = "AEM App-Integration - Processing Pipeline Factory")
    @interface Configuration {

        @AttributeDefinition(
                name = "Path",
                description = "Path to .yaml-files with pipeline definitions."
        )
        @Nonnull
        String path();
    }

    @Reference
    private TaskFactory taskFactory;

    private final String path;

    @Activate
    public AemProcessingPipelineFactory(@Nonnull Configuration configuration) {
        this.path = configuration.path();
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
    @Nonnull
    @Override
    public ProcessingPipeline createProcessingPipeline(@Nonnull AemGlobalContext context, @Nonnull String name) {
        final ResourceResolver resolver = context.getResourceResolver();

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
            final PipelineDefinition pipelineDefinition = YamlPipelineBuilder.parsePipelineDefinitionYaml(yamlInputStream);
            return YamlPipelineBuilder.build(context, taskFactory, context.getIntegrationLog().createResourceLogger("pipeline"), pipelineDefinition);
        } catch (IOException | RuntimeException e) {
            throw new AppIntegrationException("Cannot load pipeline " + name, e);
        }
    }

}
