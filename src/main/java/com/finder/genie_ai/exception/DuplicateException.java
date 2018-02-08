package com.finder.genie_ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateException extends BaseException {

    public DuplicateException(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }

}
