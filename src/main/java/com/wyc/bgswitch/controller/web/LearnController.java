package com.wyc.bgswitch.controller.web;

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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/learn")
public class LearnController {
    private final SimpMessagingTemplate template;
    private final RedisService redisService;
    private final JwtEncoder encoder;

    private final UserRepository userRepo;
    private final CitadelGameRepository gameRepo;
    private final RoomRepository roomRepo;

    @Autowired
    public LearnController(SimpMessagingTemplate template, RedisService redisService, JwtEncoder encoder, UserRepository userRepo, CitadelGameRepository gameRepo, RoomRepository roomRepo) {
        this.template = template;
        this.redisService = redisService;
        this.encoder = encoder;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.roomRepo = roomRepo;
    }


    @PreAuthorize("hasAnyRole('WYC')")
    @GetMapping("/testRole")
    public String testRole() {
        return "66";
    }


    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name, Authentication authentication) {
//        System.out.println(authentication);
        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return "Bearer " + this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//        return String.format("lllasfasfal %s!", name);
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return "Bearer " + this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @GetMapping("/redis/list")
    public void redis() {
        redisService.addToList("test", "666");
        redisService.addToHash("test_hash", "t", "666");
        redisService.addValue("test_key2", "676");
    }

    @PostMapping("/redis/repository/saveRoom")
    public String redisRepositorySaveRoom(@RequestBody RedisRepositorySaveRequestBody body) {
//        CitadelGame game = gameRepo.save(new CitadelGame("test", Collections.singletonList(new CitadelPlayer(null, 456)), new CitadelGameConfig()));
        Room room = new Room(body.roomId(), new LinkedList<>(Collections.singletonList("8a41f3f0-ae55-4a18-8738-daaa8c0639a4")));
        CitadelGame g = new CitadelGame(body.roomId(), CitadelGameConfig.defaultConfig, Collections.singletonList(new CitadelPlayer(null, 456)));
        gameRepo.save(g);
        room.setGame(g);
        room = roomRepo.save(room);
        return room.getId();
    }

    @GetMapping("/redis/repository/find")
    public Room redisRepositoryFind(@RequestParam String roomId) {
        return roomRepo.findById(roomId).orElse(null);
    }

    @GetMapping("/redis/repository/findGameByRoomId")
    public List<CitadelGame> redisRepositoryFindGameByRoomId(@RequestParam String roomId) {
        return gameRepo.findByRoomId(roomId);
    }

    private record RedisRepositorySaveRequestBody(String roomId) {
    }
}
