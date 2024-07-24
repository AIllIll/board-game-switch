package com.wyc.bgswitch.game.dcode.annotation;

import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wyc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DCodeHandler {
    DCodeGameActionType value();
}
