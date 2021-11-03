package com.bringframework.exception;

public class BoboException extends RuntimeException {
    public BoboException() {
    }

    public BoboException(String message) {
        super(message);
    }

    public BoboException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoboException(Throwable cause) {
        super(cause);
    }
}
