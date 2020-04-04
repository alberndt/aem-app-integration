package com.alexanderberndt.appintegration.engine.processors.html.api;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;

public class IntegrationMessage {

    public enum Type {INFO, WARNING, ERROR}

    private final Type type;
    private final String message;
    private final Throwable throwable;

    public IntegrationMessage(Type type, String message, Throwable throwable) {
        this.type = type;
        this.message = message;
        this.throwable = throwable;
    }

    public static IntegrationMessage createMessage(IntegrationMessage.Type type, String messagePattern, Object... objects) {
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
            return new IntegrationMessage(type, formattingTuple.getMessage(), t);
        } else {
            formattingTuple = MessageFormatter.arrayFormat(messagePattern, objects);
            return new IntegrationMessage(type, formattingTuple.getMessage(), null);
        }
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
