package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IntegrationLogTest {

    @Test
    void createSubLogger() throws IOException {
        IntegrationLog mainLogger = new IntegrationLog();
        mainLogger.addWarning("start something");

        ExternalResourceRef resourceRef = new ExternalResourceRef("http://www.example.com:8080/blog/2020/article.html?preview=full#summary", ExternalResourceType.HTML);
        ResourceLog resourceLog = mainLogger.createResourceEntry(resourceRef);

        assertEquals("www.example.com/blog/2020", resourceLog.getPath());
        assertEquals("article.html", resourceLog.getName());


        resourceLog.addWarning("detail message");
        resourceLog.addWarning("more data");

        System.out.println("Hello");
        StringWriter stringWriter = new StringWriter();
        mainLogger.writeJson(stringWriter);
        stringWriter.close();
        System.out.println(stringWriter);
        assertNotNull(mainLogger);
    }


}