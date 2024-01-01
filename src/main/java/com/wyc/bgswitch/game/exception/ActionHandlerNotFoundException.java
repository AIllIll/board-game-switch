package com.wyc.bgswitch.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wyc
 * <p>
 * Each game action type should have matching ActionHandler
 */

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Game action handler not found.")  // 500
public class ActionHandlerNotFoundException extends RuntimeException {
}
