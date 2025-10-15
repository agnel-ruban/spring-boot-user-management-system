package com.i2i.usermanagement.service;

import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.util.JwtUtil;
import org.springframework.stereotype.Service;

/**
 * Service for JWT token operations.
 * Simple service that delegates token generation to JwtUtil.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtUtil the JWT utility
     */
    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generates JWT token for user.
     *
     * @param user the user entity
     * @return JWT token string
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }
}
