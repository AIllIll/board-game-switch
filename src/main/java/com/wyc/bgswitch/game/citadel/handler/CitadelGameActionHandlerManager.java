package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.exception.ActionHandlerNotFoundException;
import com.wyc.bgswitch.game.exception.ActionUnavailableException;
import com.wyc.bgswitch.redis.entity.User;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wyc
 */
@Component
public class CitadelGameActionHandlerManager {
    private final Map<String, Object> beans;
    private final ApplicationContext applicationContext;

    @Autowired
    public CitadelGameActionHandlerManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        beans = applicationContext.getBeansWithAnnotation(Handler.class);
    }

    private ActionHandler getHandler(CitadelGameActionType actionType) {
        for (String beanName : beans.keySet()) {
            Handler a = applicationContext.findAnnotationOnBean(beanName, Handler.class);
            assert a != null;
            if (a.value().equals(actionType)) {
                return (ActionHandler) beans.get(beanName);
            }
        }
        throw new ActionHandlerNotFoundException();
    }

    public CitadelGame handleAction(CitadelGame game, CitadelGameAction action, User user) {
        // 获取handler
        ActionHandler handler = getHandler(action.getType());
        // 检查
        if (!handler.check(game, action, user)) {
            throw new ActionUnavailableException();
        }
        // 执行
        return handler.handle(game, action, user);
    }
}
