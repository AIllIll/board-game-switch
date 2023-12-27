package com.wyc.bgswitch.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wyc
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game not found.")  // 404
public class GameNotFoundException extends RuntimeException {
}
