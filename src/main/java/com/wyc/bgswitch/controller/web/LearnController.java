package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.citadel.CitadelEffect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.stream.Collectors;

@ApiRestController
@RequestMapping("/learn")
public class LearnController {
    private final SimpMessagingTemplate template;
    @Autowired
    JwtEncoder encoder;

    @Autowired
    public LearnController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @CrossOrigin
    @PreAuthorize("hasAnyRole('WYC')")
    @GetMapping("/testRole")
    public String testRole() {
        return "66";
    }

    @CrossOrigin
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

    @CrossOrigin
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

    @GetMapping("/broadcast")
    public String broadcast(@RequestParam() String msg) {
        System.out.println(123);
        this.template.convertAndSend(
                "/topic/citadel",
                new CitadelEffect(null, "", msg)
        );
        this.template.convertAndSend(
                "/bgs/citadel",
                new CitadelEffect(null, "", msg)
        );
        return msg;
    }


}
