package com.alco.searchapp.exception;

public class BaseRuntimeException extends RuntimeException {
    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
