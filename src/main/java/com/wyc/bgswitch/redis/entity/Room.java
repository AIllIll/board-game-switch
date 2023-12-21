package com.wyc.bgswitch.redis.entity;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@RedisHash("/bgs/room")
@Data
@AllArgsConstructor
public class Room {
    @Id
    private String id;
    private LinkedList<User> users;
    @Reference
    private CitadelGame game;
}
