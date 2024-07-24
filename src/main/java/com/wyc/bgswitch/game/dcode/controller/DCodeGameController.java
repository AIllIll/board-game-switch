package com.wyc.bgswitch.game.dcode.controller;

import com.wyc.bgswitch.config.lock.RedisLockPrefix;
import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.dcode.data.DCodeRepo;
import com.wyc.bgswitch.game.dcode.handler.DCodeActionHandlerManager;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.lock.LockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author wyc
 * game controller of Davinci Code
 */
@ApiRestController
@RequestMapping("/game/dcode")
public class DCodeGameController {
    private final DCodeActionHandlerManager handlerManager;
    private final LockManager lockManager;
    private final DCodeRepo repo;

    @Autowired
    public DCodeGameController(DCodeActionHandlerManager handlerManager, LockManager lockManager, DCodeRepo repo) {
        this.handlerManager = handlerManager;
        this.lockManager = lockManager;
        this.repo = repo;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 轮询同步游戏状态
     *
     * @return
     */
    @GetMapping("/fetch")
    public DCodeGame fetch(Authentication authentication) {
        String userId = authentication.getName();
        DCodeGame game = repo.find();
        game.attachInfo(userId);
        return game;
    }

    /**
     * 用户行动
     *
     * @param action
     * @param authentication
     */
    @PostMapping("/action")
    public void action(@RequestBody DCodeGameAction action, Authentication authentication) {
        action.setPlayerId(authentication.getName());
        LockManager.MultiLockBuilder.MultiLock lock = lockManager.useBuilder().obtain(RedisLockPrefix.LOCK_PREFIX_GAME).
                of("dcode"). // 目前只有一个房间
                        build();
        lock.lock();
        try {
            handlerManager.handleAction(action, authentication.getName());
        } finally {
            lock.unLock();
        }
    }

    /**
     * 重置
     *
     * @param authentication
     */
    @PostMapping("/reset")
    public void action(Authentication authentication) {
        String userId = authentication.getName();
        if(repo.find().getPlayers().stream().anyMatch(p -> p.getUserId().equals(userId))) {
            repo.save(new DCodeGame());
        }
    }
}
