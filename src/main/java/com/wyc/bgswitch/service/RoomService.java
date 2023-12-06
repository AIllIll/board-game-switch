package com.wyc.bgswitch.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wyc
 * fixme 解决线程安全问题：目前加上了最大的粒度锁，待优化
 */
@Service
public class RoomService {
    ConcurrentHashMap<String, HashSet<String>> userRoomsMap;
    ConcurrentHashMap<String, LinkedList<String>> roomUsersMap; // List方便排序

    RoomService() {
        userRoomsMap = new ConcurrentHashMap<>();
        roomUsersMap = new ConcurrentHashMap<>();
    }

    /**
     * 删除房间：
     * 1. 移除房间
     * 2. 修改用户的房间集合
     *
     * @param roomId
     */
    public void destroyRoom(String roomId) {
        List<String> usersInRoom = roomUsersMap.getOrDefault(roomId, new LinkedList<>());
        for (String u : usersInRoom) {
            userRoomsMap.get(u).remove(roomId); // 删除该房间在用户中的记录
        }
        roomUsersMap.remove(roomId);
    }

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
        roomUsersMap.computeIfAbsent(roomId, k -> new LinkedList<>());
        if (!roomUsersMap.get(roomId).contains(userId)) {
            // 不可以重复添加用户
            roomUsersMap.get(roomId).add(userId);
        }
    }

    /**
     * 离开房间
     * 1. 从用户的房间集合中移除
     * 2. 从房间的用户列表中移除
     *
     * @param userId
     * @param roomId
     */
    synchronized public void userLeaveRoom(String userId, String roomId) {
        Optional.ofNullable(userRoomsMap.get(userId)).ifPresent(roomSet -> roomSet.remove(roomId));
        Optional.ofNullable(roomUsersMap.get(roomId)).ifPresent(userList -> userList.remove(userId));
    }

    public List<String> getRooms() {
        // 防止修改
        return roomUsersMap.keySet().stream().toList();
    }

    public List<String> getUsers() {
        // 防止修改
        return userRoomsMap.keySet().stream().toList();
    }

    public List<String> getRoomUsers(String roomId) {
        // 防止修改
        return roomUsersMap.get(roomId).stream().toList();
    }

    public List<String> getUserRooms(String userId) {
        // 防止修改
        return userRoomsMap.get(userId).stream().toList();
    }

}
