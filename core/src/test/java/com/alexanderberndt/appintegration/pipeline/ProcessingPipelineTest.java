package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.core.CoreGlobalContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.valuemap.RankedAndTypedValueMap;
import com.alexanderberndt.appintegration.tasks.load.DownloadTask;
import com.alexanderberndt.appintegration.tasks.prepare.PropertiesTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ProcessingPipelineTest {


    @Test
    void simpleProcessingPipeline() throws IOException {

        RankedAndTypedValueMap emptyProperties = new RankedAndTypedValueMap();
        CoreGlobalContext context = new CoreGlobalContext(null, emptyProperties);
        ProcessingPipeline pipeline = ProcessingPipelineBuilder.createPipelineInstance(context)
                .addTask(new PropertiesTask())
                .withTaskParam("random-input.length", 2000)
                .withTaskParam("something.else", true)
                .addTask(new DownloadTask())
                .withTaskParam("loader", "http")
                .build();

//        pipeline.addTask("add-referenced-resource", "relativeUrl", "css/master.css", "expectedType", "CSS");
//        pipeline.addTask("add-referenced-resource", "relativeUrl", "/img/GULP-btn.png", "expectedType", "binary");

        ExternalResourceRef resourceRef = new ExternalResourceRef("http://www.alexanderberndt.com", ExternalResourceType.HTML);
        ExternalResource resource = pipeline.loadAndProcessResourceRef(context, resourceRef);

        for (ExternalResourceRef ref : resource.getReferencedResources()) {
            System.out.println(ref);
            ExternalResource referenceResource = pipeline.loadAndProcessResourceRef(context, ref);
            System.out.println(referenceResource.getString());
        }

        //System.out.println(resource.getString());


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