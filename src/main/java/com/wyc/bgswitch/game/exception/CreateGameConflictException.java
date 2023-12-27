package com.wyc.bgswitch.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wyc
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "An unfinished game exists.")  // 409
public class CreateGameConflictException extends RuntimeException {
}
