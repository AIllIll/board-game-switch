package com.wyc.bgswitch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author wyc
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 409
public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}