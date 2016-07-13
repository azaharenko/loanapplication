package com.loanapp.controllers;

import com.loanapp.beans.Message;
import com.loanapp.utils.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Message processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();

        return processError(error);
    }

    private Message processError(FieldError error) {
        Message message = null;
        if (error != null) {
            message = new Message(MessageType.ERROR, error.getDefaultMessage());
        }
        return message;
    }
}
