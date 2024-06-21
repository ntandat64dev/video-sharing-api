package com.example.videosharingapi.util;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.expiration-days}")
    private int jwtExpirationDays;

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_SCOPE = "scope";
    private static final String SCOPE_SEPARATOR = " ";

    public String generateToken(AuthenticatedUser user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(DateUtils.addDays(new Date(), jwtExpirationDays))
                .claim(CLAIM_USER_ID, user.getUserId())
                .claim(CLAIM_SCOPE, user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(SCOPE_SEPARATOR)))
                .signWith(getJwtSecretKey())
                .compact();
    }

    public void verifyToken(String token) {
        Jwts.parser()
                .verifyWith(getJwtSecretKey())
                .build()
                .parseSignedClaims(token);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public LocalDateTime extractExpiration(String token) {
        var exprirationDate = extractClaims(token).getExpiration();
        return LocalDateTime.ofInstant(exprirationDate.toInstant(), ZoneId.systemDefault());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getJwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getJwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }
}
