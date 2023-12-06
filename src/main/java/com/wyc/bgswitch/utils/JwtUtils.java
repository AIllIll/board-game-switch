package com.wyc.bgswitch.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
@Component
public class JwtUtils {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    public JwtUtils(JwtEncoder encoder, JwtDecoder decoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    public String generateTokenFromAuth(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();
        return "Bearer " + this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Authentication generateAuthFromToken(String token) {
        return this.jwtAuthenticationConverter.convert(this.decoder.decode(token));
    }
}
