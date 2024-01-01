package com.wyc.bgswitch.game.citadel.constant;

/**
 * @author wyc
 */
public enum CitadelGameActionType {
    CONFIG("CONFIG"), // 改配置
    SIT("SIT"), // 坐下，加入游戏
    KICK("KICK"), // 踢人
    STAND("STAND"), // 旁观
    START("START"), // 开始游戏
    LEAVE("LEAVE"), // 离开游戏
    PICK_CHARACTER("PICK_CHARACTER"), // 选身份
    COLLECT_COINS("COLLECT_COINS"), // 获取硬币
    DRAW_CARDS("DRAW_CARDS"), // 获取地区卡
    RETURN_CARDS("RETURN_CARDS"), // 归还地区卡
    BUILD("BUILD"), // 建造
    USE_BUILDING("USE_BUILDING"), // 使用建筑
    USE_ABILITY("USE_ABILITY"), // 使用技能
    END_TURN("END_TURN"), // 结束回合
    GIVE_UP("GIVE_UP"), // 投降
    DEFAULT(null);

    private final String value;

    CitadelGameActionType(String value) {
        this.value = value;
    }
}
