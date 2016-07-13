package com.loanapp.controllers;

import com.loanapp.beans.Message;
import com.loanapp.utils.Constants;
import com.loanapp.utils.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @RequestMapping
    public ResponseEntity<?> forwardRequest() {
        return new ResponseEntity<>(new Message(MessageType.ERROR, Constants.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

}
