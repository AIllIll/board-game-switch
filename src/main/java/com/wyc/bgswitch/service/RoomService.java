package com.wyc.bgswitch.service;

import com.wyc.bgswitch.entity.RoomInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wyc
 * fixme 解决线程安全问题：目前加上了最大的粒度锁，待优化
 */
@Service
public class RoomService {

    private final RoomMessageService roomMessageService;
    private final ConcurrentHashMap<String, HashSet<String>> userRoomsMap;
    private final ConcurrentHashMap<String, RoomInfo> roomsMap; // List方便排序
    private final ConcurrentHashMap<String, String> roomGameMap; // List方便排序

    @Autowired
    RoomService(RoomMessageService roomMessageService) {
        this.roomMessageService = roomMessageService;
        userRoomsMap = new ConcurrentHashMap<>();
        roomsMap = new ConcurrentHashMap<>();
        roomGameMap = new ConcurrentHashMap<>();
    }

//    /**
//     * 删除房间：
//     * 1. 移除房间
//     * 2. 修改用户的房间集合
//     *
//     * @param roomId
//     */
//    public void destroyRoom(String roomId) {
//        List<String> usersInRoom = roomUsersMap.getOrDefault(roomId, new LinkedList<>());
//        for (String u : usersInRoom) {
//            userRoomsMap.get(u).remove(roomId); // 删除该房间在用户中的记录
//        }
//        roomUsersMap.remove(roomId);
//    }

    /**
     * 用户进入房间
     * 1. 添加房间到用户的房间集合
     * 2. 添加用户到房间的用户列表
     *
     * @param userId
     * @param roomId
     */
    synchronized public void userEnterRoom(String userId, String roomId) {
        userRoomsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(roomId);
        roomsMap.computeIfAbsent(roomId, k -> {
            RoomInfo roomInfo = new RoomInfo(roomId, null, new LinkedList<>());
            roomInfo.getUsers().add(userId);
            return roomInfo;
        });
        if (!roomsMap.get(roomId).getUsers().contains(userId)) {
            // 不可以重复添加用户
            roomsMap.get(roomId).getUsers().add(userId);
        }
        roomMessageService.notifyRoomChanged(roomsMap.get(roomId));
    }

    //    /**
//     * 离开房间
//     * 1. 从用户的房间集合中移除
//     * 2. 从房间的用户列表中移除
//     *
//     * @param userId
//     * @param roomId
//     */
//    synchronized public void userLeaveRoom(String userId, String roomId) {
//        Optional.ofNullable(userRoomsMap.get(userId)).ifPresent(roomSet -> roomSet.remove(roomId));
//        Optional.ofNullable(roomUsersMap.get(roomId)).ifPresent(userList -> userList.remove(userId));
//    }
//    public List<String> getRooms() {
//        // 防止修改
//        return roomUsersMap.keySet().stream().toList();
//    }
//
//    public List<String> getUsers() {
//        // 防止修改
//        return userRoomsMap.keySet().stream().toList();
//    }
    public LinkedList<String> getRoomUsers(String roomId) {
        // 防止修改
        return roomsMap.get(roomId).getUsers();
    }

    public List<String> getUserRooms(String userId) {
        // 防止修改
        return userRoomsMap.get(userId).stream().toList();
    }

    public Boolean isUserInRoom(String userId, String roomId) {
        try {
            return userRoomsMap.get(userId).contains(roomId);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isRoomOwner(String userId, String roomId) {
        try {
            return roomsMap.get(roomId).getUsers().getFirst().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    public String getRoomGame(String roomId) {
        return roomGameMap.get(roomId);
    }

    public void setRoomGame(String roomId, String gameId) {
        roomGameMap.put(roomId, gameId);
    }

}
