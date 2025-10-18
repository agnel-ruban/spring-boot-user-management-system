package com.i2i.usermanagement.util;

import com.i2i.usermanagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for JWT token generation.
 * Handles only token creation for authentication.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user for whom to generate the token
     * @return the generated JWT token string
     */
    public String generateToken(User user) {
        List<String> roles = user.getUserRoles() != null ? 
                user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toList()) : 
                Collections.emptyList();

        return Jwts.builder()
                .setSubject(user.getName()) // Username as subject
                .claim("roles", roles) // Add roles to token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts username from JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Extracts roles from JWT token.
     *
     * @param token the JWT token
     * @return list of roles, empty list if token is invalid
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractClaims(token);
            return (List<String>) claims.get("roles");
        } catch (Exception exception) {
            // Any JWT parsing/validation exception means roles cannot be extracted
            return Collections.emptyList();
        }
    }

    /**
     * Validates JWT token - checks signature and expiration.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public Boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception exception) {
            // Any JWT parsing/validation exception means token is invalid
            return false;
        }
    }

    /**
     * Extracts claims from JWT token.
     * Common method used by other extraction methods.
     *
     * @param token the JWT token
     * @return the claims from the token
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retrieves the signing key from the base64 encoded secret.
     *
     * @return the signing key
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
