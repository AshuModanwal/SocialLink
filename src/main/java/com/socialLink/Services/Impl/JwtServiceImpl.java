package com.socialLink.Services.Impl;

import com.socialLink.Services.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    // Inject your short secret (e.g. "Ashutosh") from application.properties
    @Value("${jwt.secret}")
    private String SECRET;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            // Hash your passphrase to a 256-bit byte array
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(SECRET.getBytes(StandardCharsets.UTF_8));
            // Build a proper HS256 key
            key = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }

    @Override
    public String generateToken(String email) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + 3_600_000);  // 1 hour

        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            // token was invalid or expired
            return null;
        }
    }
}
