package com.bringframework.exception;

public class AmbiguousBoboDefinitionException extends BoboException {
    public AmbiguousBoboDefinitionException() {
    }

    public AmbiguousBoboDefinitionException(String message) {
        super(message);
    }

    public AmbiguousBoboDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousBoboDefinitionException(Throwable cause) {
        super(cause);
    }
}
