package com.example.videosharingapi.util;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
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

    public UserDetails verifyToken(String token) {
        var claims = extractClaims(token);

        if (claims.getExpiration().before(new Date())) {
            throw new JwtException("Expired JWT token");
        }

        var scopesString = claims.get(CLAIM_SCOPE, String.class);
        var scopes = Arrays.stream(scopesString.split(SCOPE_SEPARATOR)).toList();

        return AuthenticatedUser.builder()
                .userId(claims.get(CLAIM_USER_ID, String.class))
                .username(claims.getSubject())
                .scopes(scopes)
                .build();
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
