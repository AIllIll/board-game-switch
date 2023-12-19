package com.wyc.bgswitch.entity;

import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@Data
@AllArgsConstructor
public class RoomInfo {
    private String id;
    private String gameId;
    private LinkedList<String> users;
}
