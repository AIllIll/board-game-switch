package com.wyc.bgswitch.game.dcode.handler;

import com.wyc.bgswitch.game.dcode.annotation.DCodeHandler;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import com.wyc.bgswitch.game.dcode.data.DCodeRepo;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.game.exception.ActionHandlerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wyc
 */
@Component
public class DCodeActionHandlerManager {
    private final Map<String, Object> beans;
    private final ApplicationContext applicationContext;
    private final DCodeRepo repo;

    @Autowired
    public DCodeActionHandlerManager(ApplicationContext applicationContext, DCodeRepo repo) {
        this.applicationContext = applicationContext;
        beans = applicationContext.getBeansWithAnnotation(DCodeHandler.class);
        this.repo = repo;
    }

    private DCodeActionHandler getHandler(DCodeGameActionType actionType) {
        for (String beanName : beans.keySet()) {
            DCodeHandler a = applicationContext.findAnnotationOnBean(beanName, DCodeHandler.class);
            assert a != null;
            if (a.value().equals(actionType)) {
                return (DCodeActionHandler) beans.get(beanName);
            }
        }
        throw new ActionHandlerNotFoundException();
    }

    public DCodeGame handleAction(DCodeGameAction action, String userId) {
        // 获取
        DCodeGame game = repo.find();
        // 获取handler
        DCodeActionHandler handler = getHandler(action.getType());
        // 检查
        handler.check(game, action, userId);
        // 执行
        DCodeGame newGame = handler.handle(game, action, userId);
        // 保存
        repo.save(newGame);
        // 补充信息：是否当前玩家，当前玩家的idx
        newGame.attachInfo(userId);
        return newGame;
    }
}
