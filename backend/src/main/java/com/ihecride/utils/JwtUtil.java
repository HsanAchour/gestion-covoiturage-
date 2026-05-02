package com.ihecride.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * Génération et validation des tokens JWT (HS256).
 * Access token : 15 min — Refresh token : 7 jours.
 */
public class JwtUtil {

    private static final String SECRET = "IHECRideSecretKey2026ChangeMeInProductionPlease!!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long ACCESS_EXP = 15L * 60 * 1000;
    private static final long REFRESH_EXP = 7L * 24 * 60 * 60 * 1000;

    public static String generateAccessToken(int userId, String email, String role) {
        return buildToken(userId, email, role, ACCESS_EXP);
    }

    public static String generateRefreshToken(int userId, String email, String role) {
        return buildToken(userId, email, role, REFRESH_EXP);
    }

    private static String buildToken(int userId, String email, String role, long expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validate(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer extractUserId(String token) {
        Claims c = validate(token);
        return c == null ? null : Integer.parseInt(c.getSubject());
    }

    public static String extractRole(String token) {
        Claims c = validate(token);
        return c == null ? null : (String) c.get("role");
    }
}
