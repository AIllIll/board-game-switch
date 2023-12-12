package com.wyc.bgswitch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
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
public class JwtService {
    private final static long EXPIRY = 60 * 60 * 24L; // 1 day
    private final static long MAX_EXPIRY = 60 * 60 * 24 * 30L; // 30 days

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    public JwtService(JwtEncoder encoder, JwtDecoder decoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    public String generateTokenFromAuth(Authentication authentication) {
        Instant now = Instant.now();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRY))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();
        return "Bearer " + this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String refreshToken(String oldToken) {
        Jwt jwt = this.decoder.decode(oldToken);
        Authentication authentication = this.jwtAuthenticationConverter.convert(jwt);
        Instant iat = Instant.parse(jwt.getClaimAsString(JwtClaimNames.IAT));
        Instant exp = Instant.now().plusSeconds(EXPIRY); // 续签
        if (exp.isAfter(iat.plusSeconds(MAX_EXPIRY))) {
            exp = iat.plusSeconds(MAX_EXPIRY); // 使用最大续签时间
        }
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(iat) // refresh
                .expiresAt(exp)
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();
        return "Bearer " + this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Authentication generateAuthFromToken(String token) {
        return this.jwtAuthenticationConverter.convert(this.decoder.decode(token));
    }
}
