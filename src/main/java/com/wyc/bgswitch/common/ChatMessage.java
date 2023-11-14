package com.wyc.bgswitch.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ChatMessage {
    private String fromUser;
    private String content;
    private String toUser;
}
