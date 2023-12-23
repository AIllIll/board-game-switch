package com.wyc.bgswitch.controller.web.learn;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.redis.entity.Room;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;
import com.wyc.bgswitch.redis.repository.CitadelGameRepository;
import com.wyc.bgswitch.redis.repository.RoomRepository;
import com.wyc.bgswitch.redis.repository.UserRepository;
import com.wyc.bgswitch.service.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Resource;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/learn/redis")
public class LearnRedisController {
    private final RedisOperations<String, Object> operations;
    private final RedisService redisService;
    private final UserRepository userRepo;
    private final CitadelGameRepository gameRepo;
    private final RoomRepository roomRepo;
    private final RedisScript<Boolean> simpleCasScript;

    private final RedisKeyValueTemplate keyValueTemplate;
    HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
    @Resource(name = "myRedisTemplate")
    private HashOperations<String, byte[], byte[]> hashOps;

    @Autowired
    public LearnRedisController(
            @Qualifier("myRedisTemplate") RedisOperations<String, Object> operations,
            RedisService redisService,
            UserRepository userRepo,
            CitadelGameRepository gameRepo,
            RoomRepository roomRepo,
            RedisScript<Boolean> simpleCasScript,
            RedisKeyValueTemplate keyValueTemplate) {
        this.operations = operations;
        this.redisService = redisService;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.roomRepo = roomRepo;
        this.simpleCasScript = simpleCasScript;
        this.keyValueTemplate = keyValueTemplate;
    }

    @GetMapping("/list")
    public void redis() {
        redisService.addToListLeft("test", "666");
        redisService.addToHash("test_hash", "t", "666");
        redisService.setValue("test_key2", "676");
    }

    /**
     * 测试创建room
     * POST到 http://localhost:8080/api/learn/redis/repository/saveRoom
     *
     * @param body
     * @return
     */
    @PostMapping("/repository/saveRoom")
    public String redisRepositorySaveRoom(@RequestBody RedisRepositorySaveRequestBody body) {
//        CitadelGame game = gameRepo.save(new CitadelGame("test", Collections.singletonList(new CitadelPlayer(null, 456)), new CitadelGameConfig()));
        Room room = new Room(body.roomId(), new LinkedList<>(Collections.singletonList("8a41f3f0-ae55-4a18-8738-daaa8c0639a4")));
        CitadelGame g = new CitadelGame(body.roomId(), CitadelGameConfig.defaultConfig, Collections.singletonList(new CitadelPlayer(null, 456)));
        gameRepo.save(g);
        room.setGame(g);
        room = roomRepo.save(room);
        return room.getId();
    }

    /**
     * 测试通过roomId找到room
     * http://localhost:8080/api/learn/redis/repository/find?roomId=666
     * 结果是可以
     *
     * @param roomId
     * @return
     */
    @GetMapping("/repository/find")
    public Room redisRepositoryFind(@RequestParam String roomId) {
        return roomRepo.findById(roomId).orElse(null);
    }

    /**
     * 测试是否可以根据indexed字段找到目标
     * http://localhost:8080/api/learn/redis/repository/findGameByRoomId?roomId=666
     * 结果是可以
     *
     * @param roomId
     * @return
     */
    @GetMapping("/repository/findGameByRoomId")
    public List<CitadelGame> redisRepositoryFindGameByRoomId(@RequestParam String roomId) {
        return gameRepo.findByRoomId(roomId);
    }

    /**
     * 测试redisTemplate的线程安全:
     * Chrome打开一个匿名模式和一个普通模式的窗口，不能全用普通，因为浏览器会合并你的请求为同一个http连接，那样就触发不了多线程
     * 同时访问：http://localhost:8080/api/learn/redis/threadSafety?increasement=10000
     * 会发现线程不安全
     *
     * @param increment
     */
    @GetMapping("/threadSafety")
    public void threadSafety(@RequestParam Integer increment) {
        String redisKey = "/test/threadSafety";
        for (int i = 0; i < increment; i++) {
            String numStr = (String) operations.opsForValue().get(redisKey);
            System.out.println(numStr);
            int num;
            if (numStr == null) {
                num = 0;
            } else {
                num = Integer.parseInt(numStr);
            }
            num += 1;
            operations.opsForValue().set(redisKey, Integer.toString(num));
        }
    }

    /**
     * 测试lua，通过cas+失败重试来实现线程安全
     * 同时访问：http://localhost:8080/api/learn/redis/lua?increment=10000
     * 返回值是真实的尝试次数，可以看见比10000多很多，但是结果是正确的
     *
     * @param increment
     * @return
     */
    @GetMapping("/lua")
    public int lua(@RequestParam Integer increment) {
        String redisKey = "/test/threadSafety2";
        int cnt = 0;
        for (int i = 0; i < increment; i++) {
            cnt++;
            Integer numStr = (Integer) operations.opsForValue().get(redisKey);
//            String numStr = (String) operations.opsForValue().get(redisKey);
            int num;
            if (numStr == null) {
                num = 0;
            } else {
//                num = Integer.parseInt(numStr);
                num = numStr;
            }
            System.out.println(numStr);
            Boolean res = operations.execute(
                    simpleCasScript,
                    Collections.singletonList(redisKey),
                    num, (num + 1), true
            );
            if (!res) {
                i--;
            }
            if (cnt > 3 * increment) break;
        }
        return cnt;
    }

    /**
     * 上面的测试lua中，通过cas+失败重试来实现线程安全已经验证成功，现在测试cas是否对于object也有效果。
     * 将value序列化方法设置为RedisSerializer.json()
     * 同时访问：http://localhost:8080/api/learn/redis/lua2?increment=10000
     * 返回值是真实的尝试次数，可以看见也是比10000多很多，但是结果是正确的
     * 查看redis中的存储对象：
     * "{\"@class\":\"com.wyc.bgswitch.controller.web.learn.LearnRedisController$Lua2TestObj\",\"num\":9999}"
     *
     * @param increment
     * @return
     */
    @GetMapping("/lua2")
    public int lua2(@RequestParam Integer increment) {
        String redisKey = "/test/threadSafety3";
        int cnt = 0;
        for (int i = 0; i < increment; i++) {
            cnt++;
            Lua2TestObj obj = (Lua2TestObj) operations.opsForValue().get(redisKey);
            Lua2TestObj newObj;
            if (obj == null) {
                newObj = new Lua2TestObj(0);
            } else {
                newObj = new Lua2TestObj(obj.num + 1);
            }
            System.out.println(newObj.num);
            Boolean res = operations.execute(
                    simpleCasScript,
                    Collections.singletonList(redisKey),
                    obj, newObj, true
            );
            if (!res) {
                i--;
            }
            if (cnt > 3 * increment) break;
        }
        return cnt;
    }

    /**
     * 上面的测试lua1和2中，通过cas+失败重试来实现线程安全已经验证成功
     * 现在测试RedisSerializer.json()的序列化结果中，@Class字段是否会有不良影响
     * 先访问：http://localhost:8080/api/learn/redis/lua2?increment=10000
     * 创建了redis对象后，
     * 再访问：http://localhost:8080/api/learn/redis/serializer
     * 结果是会存在影响的，无法进行转换。
     *
     * @return
     */
    @GetMapping("/serializer")
    @Deprecated
    public Lua2TestObj2 serializer() {
        String redisKey = "/test/threadSafety3";
        return (Lua2TestObj2) operations.opsForValue().get(redisKey);
    }

    /**
     * 上面的测试serializer中，发现RedisSerializer.json()不符合我们的要求，但是其实repo中的serializer是可以达成我们的要求的
     * 现在尝试默认的Serializer，通过添加Room类型的value来对比roomRepo中保存的room
     * 访问 http://localhost:8080/api/learn/redis/serializer2?roomId=1
     * redis: hkeys /test/serializer2
     * 失败。
     * 通过KeyValueTemplate可以实现和repo类似的效果——通过Entity的注解确定keySpace，然后去crud
     * 但是无论是repo还是kvTemplate，其实现都不是线程安全的，另外，想用lua去修改某个repo，还要处理ref和index，非常麻烦
     * 结论： redis本身还是用来做缓存的，要线程安全，只能是cas（因为锁也很麻烦，而且锁也要cas），所以必须用lua脚本。
     *
     * @return
     */
    @GetMapping("/serializer2")
    @Deprecated
    public Room serializer2(@RequestParam String roomId) {
        // repo保存
        Room room = new Room(roomId, new LinkedList<>(Collections.singletonList("8a41f3f0-ae55-4a18-8738-daaa8c0639a4")));
        CitadelGame g = new CitadelGame(roomId, CitadelGameConfig.defaultConfig, Collections.singletonList(new CitadelPlayer(null, 456)));
        gameRepo.save(g);
        room.setGame(g);
        room = roomRepo.save(room);
        // template保存
        Room room1 = keyValueTemplate.insert("/test/serializer2", room);
        return keyValueTemplate.findById(roomId, Room.class).orElse(null);
    }

    /**
     * 测试能否用hash的template读取repo保存的对象
     * 访问 http://localhost:8080/api/learn/redis/serializer3?roomId=1
     * 失败
     *
     * @param roomId
     * @return
     */
    @GetMapping("/serializer3")
    @Deprecated
    public Room serializer3(@RequestParam String roomId) {

        return (Room) mapper.fromHash(hashOps.entries("/bgs/room:" + roomId));
    }

    private record Lua2TestObj(Integer num) implements Serializable {
    }

    private record Lua2TestObj2(Integer num) implements Serializable {
    }

    private record RedisRepositorySaveRequestBody(String roomId) implements Serializable {
    }
}
