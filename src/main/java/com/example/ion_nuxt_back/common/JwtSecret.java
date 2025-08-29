package com.example.ion_nuxt_back.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtSecret {
    private static Key key = null;
    private static final long accessExpire = 1000 * 60 * 30;;
    private static final long refreshExpire = 1000 * 60 * 60 * 24 * 7;
    public JwtSecret(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateAccessToken(String account) {
        return Jwts.builder()
                .setSubject(account)
                .setExpiration(new java.util.Date(System.currentTimeMillis() + accessExpire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public static String generateRefreshToken(String account) {
        return Jwts.builder()
                .setSubject(account)
                .setExpiration(new java.util.Date(System.currentTimeMillis() + refreshExpire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
