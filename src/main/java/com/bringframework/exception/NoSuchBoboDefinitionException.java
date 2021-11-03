package com.bringframework.exception;

public class NoSuchBoboDefinitionException extends BoboException {
    public NoSuchBoboDefinitionException() {
    }

    public NoSuchBoboDefinitionException(String message) {
        super(message);
    }

    public NoSuchBoboDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBoboDefinitionException(Throwable cause) {
        super(cause);
    }
}
