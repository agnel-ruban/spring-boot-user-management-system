package com.i2i.usermanagement.service;

import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.dto.AuthResponseDTO;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.exception.AuthenticationException;
import com.i2i.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for authentication operations.
 * Handles user login and JWT token generation.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     * @param jwtService the JWT service
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates user and generates JWT token.
     *
     * @param authRequest the authentication request
     * @return authentication response with JWT token
     * @throws AuthenticationException if authentication fails
     */
    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        // Find user by username (name field)
        User user = userRepository.findByNameAndIsActiveTrue(authRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        // Check password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Return authentication response
        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .build();
    }
}
