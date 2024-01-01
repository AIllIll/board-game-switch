package com.wyc.bgswitch.service;

import static com.wyc.bgswitch.config.lock.RedisLockPrefix.LOCK_PREFIX_ROOM_USERS;
import static com.wyc.bgswitch.config.lock.RedisLockPrefix.LOCK_PREFIX_USER_ROOMS;

import com.wyc.bgswitch.entity.RoomInfoVO;
import com.wyc.bgswitch.lock.LockManager;
import com.wyc.bgswitch.redis.zset.RoomGamesZSetManager;
import com.wyc.bgswitch.redis.zset.RoomUsersZSetManager;
import com.wyc.bgswitch.redis.zset.UserRoomsZSetManager;
import com.wyc.bgswitch.service.message.RoomMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyc
 * fixme 解决线程安全问题：目前加上了最大的粒度锁，待优化
 */
@Service
public class RoomService {
    private final LockManager lockManager;
    private final RoomMessageService roomMessageService;
    private final RedisLockRegistry redisLockRegistry;
    private final RoomUsersZSetManager roomUsersZSetManager;
    private final UserRoomsZSetManager userRoomsZSetManager;
    private final RoomGamesZSetManager roomGamesZSetManager;

    @Autowired
    RoomService(
            LockManager lockManager,
            RoomMessageService roomMessageService,
            RedisLockRegistry redisLockRegistry,
            RoomUsersZSetManager roomUsersZSetManager,
            UserRoomsZSetManager userRoomsZSetManager,
            RoomGamesZSetManager roomGamesZSetManager
    ) {
        this.lockManager = lockManager;
        this.roomMessageService = roomMessageService;
        this.redisLockRegistry = redisLockRegistry;
        this.roomUsersZSetManager = roomUsersZSetManager;
        this.userRoomsZSetManager = userRoomsZSetManager;
        this.roomGamesZSetManager = roomGamesZSetManager;
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
    public void userEnterRoom(String userId, String roomId) {
        LockManager.MultiLockBuilder.MultiLock lock = lockManager.useBuilder()
                .obtain(LOCK_PREFIX_USER_ROOMS).of(userId)
                .obtain(LOCK_PREFIX_ROOM_USERS).of(roomId)
                .build();
        lock.lock();
        try {
            userRoomsZSetManager.add(userId, roomId);
            roomUsersZSetManager.add(roomId, userId);
        } finally {
            lock.unLock();
        }
    }

    public List<String> getRoomUserIds(String roomId) {
        return roomUsersZSetManager.getAll(roomId);
    }

    public List<String> getUserRooms(String userId) {
        return userRoomsZSetManager.getAll(userId);
    }

    public Boolean isUserInRoom(String userId, String roomId) {
        return roomUsersZSetManager.in(roomId, userId);
    }

    public Boolean isRoomOwner(String userId, String roomId) {
        return userId.equals(roomUsersZSetManager.getFirst(roomId));
    }

    /**
     * @param roomId
     * @return gameIdWithPrefix
     */
    public String getLastGameId(String roomId) {
        return roomGamesZSetManager.getLast(roomId);
    }

    /**
     * @param roomId
     * @param gameIdWithPrefix
     */
    public void attachGameToRoom(String roomId, String gameIdWithPrefix) {
        roomGamesZSetManager.add(roomId, gameIdWithPrefix);
    }

    public List<String> getGames(String roomId) {
        return roomGamesZSetManager.getAll(roomId);
    }


    public RoomInfoVO getRoomInfo(String roomId) {
        return new RoomInfoVO(
                roomId,
                getRoomUserIds(roomId),
                getLastGameId(roomId)
        );
    }
}
