package com.example.goonthug_demo_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:3600000}") // Значение по умолчанию: 1 час
    private long expiration;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (SECRET_KEY.getBytes().length < 32) {
            logger.warn("SECRET_KEY is too short, generating a secure key...");
            signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            SECRET_KEY = Base64.getEncoder().encodeToString(signingKey.getEncoded());
            logger.info("Generated secure key (base64): {}", SECRET_KEY);
        } else {
            signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }
        logger.debug("Initialized signingKey with length: {} bits", signingKey.getEncoded().length * 8);
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Invalid token format: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid token signature: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error parsing token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            Claims claims = extractAllClaims(token);
            boolean isValid = claims.getSubject().equals(email) && !isTokenExpired(token);
            logger.debug("Token validation for email {}: {}", email, isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractAllClaims(token).getExpiration();
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
            return true;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}