package com.bring.library.exception;

public class EmptyDirectoryException extends RuntimeException{
    public EmptyDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
