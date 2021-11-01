package com.bringframework.exception;

public class ScanPackageException extends RuntimeException{
    public ScanPackageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScanPackageException(String message) {
        super(message);
    }
}
