package com.wyc.bgswitch.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author wyc
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
public class ActionUnavailableException extends RuntimeException {
    public ActionUnavailableException() {
        super("Unavailable game action.");
    }

    public ActionUnavailableException(String message) {
        super(message);
    }
}