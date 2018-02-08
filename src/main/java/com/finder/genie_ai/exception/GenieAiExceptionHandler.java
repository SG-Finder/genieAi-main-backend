package com.finder.genie_ai.exception;

import com.finder.genie_ai.model.error.ErrorModel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GenieAiExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public @ResponseBody ErrorModel handleException(BaseException e) {
        return new ErrorModel(e.getStatusCode(), e.getMessage());
    }

}
