package com.alexanderberndt.appintegration.exceptions;

/**
 *
 */
public class AppIntegrationException extends RuntimeException {

    private static final long serialVersionUID = -1893166918576102752L;

    public AppIntegrationException(String message) {
        super(message);
    }

    public AppIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
