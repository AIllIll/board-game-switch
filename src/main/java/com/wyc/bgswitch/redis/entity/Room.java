package com.wyc.bgswitch.redis.entity;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedList;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@RedisHash("/bgs/room")
@Data
public class Room {
    @Id
    @NonNull
    private String id;
    @NonNull
    private LinkedList<String> users;
    @Reference
    private CitadelGame game;
}
