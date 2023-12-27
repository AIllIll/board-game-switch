package com.wyc.bgswitch.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@Data
@AllArgsConstructor
public class RoomInfoVO {
    private String id;
    private List<String> userIds;
    private String gameId;
}
