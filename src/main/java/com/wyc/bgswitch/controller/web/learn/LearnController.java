package com.wyc.bgswitch.controller.web.learn;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/learn")
public class LearnController {
    private final JwtEncoder encoder;

    @Value("${version}")
    private String version;


    @Autowired
    public LearnController(JwtEncoder encoder) {

        this.encoder = encoder;
    }


    @PreAuthorize("hasAnyRole('WYC')")
    @GetMapping("/testRole")
    public String testRole() {
        return "66";
    }


    @GetMapping("/hello")
    public String hello() {
        return "hello \nversion: %s".formatted(version);
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
}
