package com.bring.project.exception;

public class EmptyDirectoryException extends RuntimeException{
    public EmptyDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
