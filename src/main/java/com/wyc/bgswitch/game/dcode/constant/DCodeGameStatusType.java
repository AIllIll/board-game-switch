package com.wyc.bgswitch.game.dcode.constant;

public enum DCodeGameStatusType {

    PREPARE("PREPARE") // 改配置
    ,
    SETUP("SETUP") // 初始抽卡
    ,
    ONGOING("ONGOING") // 抽卡和猜测
    ,
    END("END") // 改配置
    ;

    private final String value;

    DCodeGameStatusType(String value) {
        this.value = value;
    }
}
