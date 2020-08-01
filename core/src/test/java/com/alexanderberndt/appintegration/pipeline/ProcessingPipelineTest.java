package com.alexanderberndt.appintegration.pipeline;

import com.alexanderberndt.appintegration.core.CoreGlobalContext;
import com.alexanderberndt.appintegration.core.CoreTaskFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.valuemap.ValueMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ProcessingPipelineTest {

    @Test
    void addTaskWithSuccess() {

    }

    @Test
    void simpleProcessingPipeline() throws IOException {

        ValueMap emptyProperties = new ValueMap();
        CoreGlobalContext context = new CoreGlobalContext(null, emptyProperties);
        ProcessingPipeline pipeline = new ProcessingPipeline(new CoreTaskFactory(), emptyProperties);

        pipeline.addTask("properties", "random-input.length", 2000);
        pipeline.addTask("download", "loader", "http");
        pipeline.addTask("add-referenced-resource", "relativeUrl", "css/master.css", "expectedType", "CSS");
        pipeline.addTask("add-referenced-resource", "relativeUrl", "/img/GULP-btn.png", "expectedType", "binary");

        ExternalResourceRef resourceRef = new ExternalResourceRef("http://www.alexanderberndt.com", ExternalResourceType.HTML);
        ExternalResource resource = pipeline.load(context, resourceRef);

        for (ExternalResourceRef ref : resource.getReferencedResources()) {
            System.out.println(ref);
            ExternalResource referenceResource = pipeline.load(context, ref);
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

    @Test
    void load() {
    }


}