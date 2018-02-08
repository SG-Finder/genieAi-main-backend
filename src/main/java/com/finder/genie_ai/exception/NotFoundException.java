package com.finder.genie_ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }
}
