package com.finder.genie_ai.exception;

import org.springframework.http.HttpStatus;

public class ServerException extends BaseException {

    public ServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

}
