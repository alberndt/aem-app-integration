package com.alexanderberndt.appintegration.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;

import static org.junit.Assert.*;

public class IntegrationContextTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationContextTest.class);

    @Test
    public void addWarning() {

        Throwable t2 = new RuntimeException();

        Object[] objects = new Object[] {"Alex", "B", t2};
        String messagePattern = "Here comes {} - {}!";

        final FormattingTuple formattingTuple;
        if ((objects != null) && (objects.length > 0) && (objects[objects.length - 1] instanceof Throwable)) {
            // format message, if it contains a throwable
            final Throwable t = (Throwable) objects[objects.length - 1];
            final Object[] otherObjects;
            if (objects.length > 1) {
                otherObjects = Arrays.copyOfRange(objects, 0, objects.length - 1);
            } else {
                otherObjects = null;
            }
            formattingTuple = MessageFormatter.arrayFormat(messagePattern, otherObjects, t);
        } else {
            formattingTuple = MessageFormatter.arrayFormat(messagePattern, objects);
        }

        System.out.println(formattingTuple.getMessage());
        formattingTuple.getThrowable().printStackTrace(System.out);
        LOGGER.error(messagePattern, objects);


    }

}