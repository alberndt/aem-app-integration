package com.alexanderberndt.appintegration.engine.logging;

import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

class IntegrationLoggerTest {


    @Test
    void createSubLogger() throws IOException {

        LogAppender appenderMock = Mockito.spy(new Slf4jLogAppender());

        IntegrationLogger integrationLogger = new IntegrationLogger(appenderMock);
        integrationLogger.addWarning("start something");

        Mockito.verify(appenderMock).appendLogEntry(integrationLogger, LogStatus.WARNING, "start something");

        ExternalResourceRef resourceRef = new ExternalResourceRef("http://www.example.com:8080/blog/2020/article.html?preview=full#summary", ExternalResourceType.HTML);
        ResourceLogger resourceLogger = integrationLogger.createResourceLogger(resourceRef);

//        assertEquals("www.example.com/blog/2020", resourceLogger.getLoggerPath());
//        assertEquals("article.html", resourceLogger.getName());


        resourceLogger.addWarning("detail message");
        resourceLogger.addWarning("more data");

//        System.out.println("Hello");
//        StringWriter stringWriter = new StringWriter();
//        integrationLogger.writeJson(stringWriter);
//        stringWriter.close();
//        System.out.println(stringWriter);
//        assertNotNull(integrationLogger);


    }


}