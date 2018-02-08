package com.finder.genie_ai.exception;

public class BaseException extends RuntimeException {

    private int statusCode;

    public BaseException(int statusCode) {
        this(statusCode, null);
    }

    public BaseException(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public BaseException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
