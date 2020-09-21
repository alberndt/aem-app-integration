package com.alexanderberndt.appintegration.engine.logging.appender;

import com.alexanderberndt.appintegration.engine.logging.AbstractLogger;
import com.alexanderberndt.appintegration.engine.logging.LogStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractLogAppenderTest {

    private JsonLogAppender jsonLogAppender;

    private StringWriter stringWriter;

    @Mock
    private AbstractLogger loggerMock;

    @Mock
    private JsonLogAppender.JsonLogEntry parentEntry;

    @BeforeEach
    void beforeEach() {
        this.stringWriter = new StringWriter();
        this.jsonLogAppender = new JsonLogAppender(() -> this.stringWriter);
        lenient().when(this.loggerMock.getType()).thenReturn("something");
    }

    @Test
    void createNewLogEntry() {
        final JsonLogAppender.JsonLogEntry newLogEntry = jsonLogAppender.createNewLogEntry(parentEntry, loggerMock);
        assertNotNull(newLogEntry);
        verify(parentEntry).registerSubEntry(newLogEntry);
    }

    @Test
    void createNewLogMessage() {
        jsonLogAppender.createNewLogMessage(parentEntry, LogStatus.INFO, "Hello World");
        verify(parentEntry).registerSubEntry(any());
    }

    @Test
    void appendLogger() throws IOException {
        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.close();

        assertEquals("[{'type':'something'}]", getJson());
    }

    @Test
    void setLoggerSummary() throws IOException {
        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.setLoggerSummary(loggerMock, LogStatus.WARNING, "this_is_my_summary!");
        jsonLogAppender.close();

        assertEquals("[{'type':'something','status':'WARNING','message':'this_is_my_summary!'}]", getJson());
    }

    @Test
    void setLoggerStatus() throws IOException {
        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.setLoggerStatus(loggerMock, LogStatus.ERROR);
        jsonLogAppender.close();

        assertEquals("[{'type':'something','status':'ERROR'}]", getJson());
    }

    @Test
    void setLoggerProperty() throws IOException {
        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.setLoggerProperty(loggerMock, "myKey", "myValue");
        jsonLogAppender.close();

        assertEquals("[{'type':'something','properties':{'myKey':'myValue'}}]", getJson());
    }

    @Test
    void appendLogEntry() throws IOException {
        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.appendLogEntry(loggerMock, LogStatus.WARNING, "this_test_is_a_shame!");
        jsonLogAppender.close();

        assertEquals("[{'type':'something','entries':[{'type':'message','status':'WARNING','message':'this_test_is_a_shame!'}]}]", getJson());
    }


    @Test
    void multiLoggerTest() throws IOException {
        AbstractLogger loggerMock2 = mock(AbstractLogger.class);
        when(loggerMock2.getType()).thenReturn("something-else");

        jsonLogAppender.appendLogger(loggerMock);
        jsonLogAppender.appendLogger(loggerMock2);

        jsonLogAppender.appendLogEntry(loggerMock, LogStatus.WARNING, "why_this!");
        jsonLogAppender.appendLogEntry(loggerMock2, LogStatus.WARNING, "and_this!");
        jsonLogAppender.appendLogEntry(loggerMock2, LogStatus.WARNING, "and_this2!");

        jsonLogAppender.setLoggerSummary(loggerMock2, LogStatus.INFO, "summary2");
        jsonLogAppender.setLoggerProperty(loggerMock, "Here", "we_are");

        jsonLogAppender.close();


        assertEquals("[{'type':'something','properties':{'Here':'we_are'},'entries':[{'type':'message','status':'WARNING','message':'why_this!'}]},"
                + "{'type':'something-else','status':'INFO','message':'summary2','entries':[{'type':'message','status':'WARNING','message':'and_this!'},{'type':'message','status':'WARNING','message':'and_this2!'}]}]", getJson());
    }

    @Nonnull
    private String getJson() {
        return stringWriter.toString().replaceAll("[\\s\\r\\n]", "").replaceAll("\"", "'");
    }


}