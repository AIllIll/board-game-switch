package com.wyc.bgswitch.controller.websocket;

import com.wyc.bgswitch.entity.ChatMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@MessageMapping("/common")
public class CommonWsController {
    private final SimpMessagingTemplate messaging;

    @Autowired
    public CommonWsController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    /**
     * 经典订阅式场景，server充当simple broker
     * client发送给 /bgs/common/broadcast
     * 返回给 /common/broadcast
     * (如果不加@SendTo则默认返回给 /topic/common/broadcast)
     * （如果不是为了在中间进行拦截，其实让client直接发送给 /topic/common/broadcast 就更直接）
     *
     * @param msg
     * @return
     */
    @MessageMapping("/broadcast")
    @PreAuthorize("hasRole('USER2')")
    @SendTo("/common/broadcast")
    public ChatMessage broadcast(ChatMessage msg, Principal principal) {
        if (principal != null)
            msg.setFromUser(principal.getName());
        return msg;
    }

    /**
     * 和上面的效果一致
     *
     * @param msg
     */
    @MessageMapping("/broadcast2")
    @PreAuthorize("hasRole('WYC')")
    public void broadcast2(ChatMessage msg, Principal principal) {
        if (principal != null)
            msg.setFromUser(principal.getName());
        this.messaging.convertAndSend(
                "/common/broadcast",
                msg
        );
    }

    /**
     * 经典的私人会话场景
     * client发送给 /bgs/common/chat
     * 提取目标用户信息后通过 /user/common/chat 发送给目标用户
     *
     * @param msg
     * @return
     */
    @MessageMapping("/chat")
    public void message(ChatMessage msg, Principal principal) {
        if (principal != null)
            msg.setFromUser(principal.getName());
        this.messaging.convertAndSendToUser(msg.getToUser(),
                "/common/chat",
                msg
        );
    }

    /**
     * 经典的客服提问场景
     * client发送给 /bgs/common/question
     * 返回给 /user/common/question
     * 如果broadcast=false，则返回给这一个用户的当前session
     * 如果broadcast=true，则返回给这一个用户的所有session
     *
     * @param question
     * @return
     */
    @MessageMapping("/question")
    @SendToUser(value = "/common/question", broadcast = true)
    public ChatMessage question(ChatMessage question) {
        // TODO do something with the question
        return question;
    }
}
