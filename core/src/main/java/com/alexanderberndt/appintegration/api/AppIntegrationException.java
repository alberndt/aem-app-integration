package com.alexanderberndt.appintegration.api;

/**
 *
 */
public class AppIntegrationException extends RuntimeException {

    public AppIntegrationException() {
    }

    public AppIntegrationException(String message) {
        super(message);
    }

    public AppIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppIntegrationException(Throwable cause) {
        super(cause);
    }

    public AppIntegrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
