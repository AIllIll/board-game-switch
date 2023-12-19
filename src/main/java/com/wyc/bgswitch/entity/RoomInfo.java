package com.wyc.bgswitch.entity;

import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wyc
 */
@Getter
@Setter
@AllArgsConstructor
public class RoomInfo {
    String id;
    String gameId;
    LinkedList<String> users;
}
