package com.alexanderberndt.appintegration.pipeline.valuemap;

public class ValueException extends Exception {

    public ValueException(String message) {
        super(message);
    }

    public ValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
