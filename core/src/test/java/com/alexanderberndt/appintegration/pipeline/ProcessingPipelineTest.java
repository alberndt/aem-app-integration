package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.core.CoreGlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.engine.resources.loader.impl.SystemResourceLoader;
import com.alexanderberndt.appintegration.tasks.load.DownloadTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import com.alexanderberndt.appintegration.tasks.process.AddReferencedResourceTask;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

class ProcessingPipelineTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    @Disabled
    void simpleProcessingPipeline() throws IOException {

        CoreGlobalContext context = new CoreGlobalContext(new SystemResourceLoader());
        ProcessingPipeline pipeline = ProcessingPipeline.createPipelineInstance(context)
                .addTask(new PropertiesTask())
                .withTaskParam("random-input.length", 2000)
                .withTaskParam("something.else", true)
                .addTask(new DownloadTask())
                .withTaskParam("loader", "http")
                .addTask(new AddReferencedResourceTask())
                .withTaskParam("relativeUrl", "resources/text1.txt")
                .withTaskParam("expectedType", "TEXT")
                .build();



//        pipeline.addTask("add-referenced-resource", "relativeUrl", "css/master.css", "expectedType", "CSS");
//        pipeline.addTask("add-referenced-resource", "relativeUrl", "/img/GULP-btn.png", "expectedType", "binary");

        ExternalResourceRef resourceRef = new ExternalResourceRef("simple-app1/server/application-info.json", ExternalResourceType.APPLICATION_PROPERTIES);
        ExternalResource resource = pipeline.loadAndProcessResourceRef(context, resourceRef);


        for (ExternalResourceRef ref : resource.getReferencedResources()) {
            System.out.println("found reference: " + ref);
            ExternalResource referenceResource = pipeline.loadAndProcessResourceRef(context, ref);
            System.out.println("loaded reference: " + referenceResource.getContentAsString());
        }

        System.out.println("loaded main resource: " + resource.getContentAsString());


//                InputStream input1 = ClassLoader.getSystemResourceAsStream("simple-app1/server/resources/to-be-filtered1.txt");
//
//        StreamToReaderConverter streamToReaderConverter = new StreamToReaderConverter();
//
//        Reader input2 = streamToReaderConverter.filter(null, input1);
//
//        TextSnippetExtractor textSnippetExtractor = new TextSnippetExtractor();
//
//        Reader input3 = textSnippetExtractor.filter(null, input2);
//
//        StringWriter result = new StringWriter();
//
//        IOUtils.copy(input3, result);
//
//        System.out.println("----");
//        System.out.print(result);
//        System.out.println("----");
//
//        assertNotNull(result);

    }


}