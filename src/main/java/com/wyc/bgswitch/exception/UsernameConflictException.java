package com.wyc.bgswitch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wyc
 */
@ResponseStatus(value = HttpStatus.CONFLICT)  // 409
public class UsernameConflictException extends RuntimeException {
    public UsernameConflictException(String message) {
        super(message);
    }
}
