package com.wyc.bgswitch.game.dcode.data;

import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import org.springframework.stereotype.Component;

/**
 * 临时DCode存储
 */
@Component
public class DCodeRepo {
    private DCodeGame game;

    public DCodeGame find() {
        if (game == null) {
            game = new DCodeGame();
            game.reset();
        }
        return game.clone();
    }

    public void save(DCodeGame game) {
        this.game = game;
    }
}
