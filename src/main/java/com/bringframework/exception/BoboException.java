package com.bringframework.exception;

public class BoboException extends RuntimeException {

    public BoboException(String message) {
        super(message);
    }

    public BoboException(String message, Throwable cause) {
        super(message, cause);
    }

}
