package com.wyc.bgswitch.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author wyc
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String fromUser; // 消息的来源
    private String content; // 消息的文本内容
    private Boolean toLobby; // 发送时的标记，决定谁会收到（订阅的destination）
    private String toRoom; // 发送时的标记，决定谁会收到（订阅的destination）
    private String toUser; // 发送时的标记，决定谁会收到（订阅的destination）
    private Boolean isSystemMsg = false; // 是否系统发出的消息
    private Long createdAt = new Date().getTime();
}
