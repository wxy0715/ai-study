package com.mmg.medical_agent.config;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public String handleValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldError().getDefaultMessage();
    }
}
