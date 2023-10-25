package com.alco.searchapp.exception;

public class TopicNotFoundException extends BaseRuntimeException {
    public TopicNotFoundException(String message) {
        super(message, null);
    }
}
