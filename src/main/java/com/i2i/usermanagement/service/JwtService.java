package com.i2i.usermanagement.service;

import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.exception.AuthenticationException;
import com.i2i.usermanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for JWT token operations with Redis caching.
 * Stores JWT tokens in Redis with TTL for fast validation.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Service
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtUtil       the JWT utility
     * @param redisTemplate the Redis template
     */
    public JwtService(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generates JWT token for user and stores in Redis.
     *
     * @param user the user entity
     * @return JWT token string
     */
    public String generateToken(User user) {
        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Store in Redis with TTL (expiration time in milliseconds)
        String redisKey = "jwt:" + token;
        redisTemplate.opsForValue().set(redisKey, user.getId(), expiration, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * Validates JWT token by checking Redis first, then JWT signature.
     *
     * @param token the JWT token
     */
    public void validateToken(String token) {
        // Check Redis first (fast lookup)
        String redisKey = "jwt:" + token;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AuthenticationException("Token expired or invalid");
        }

        // Validate JWT signature (security)
        if (!jwtUtil.isTokenValid(token)) {
            throw new AuthenticationException("Invalid JWT signature");
        }
    }
}
