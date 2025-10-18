package com.i2i.usermanagement.integration;

import com.i2i.usermanagement.controller.AuthController;
import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.dto.AuthResponseDTO;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.exception.AuthenticationException;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for Authentication.
 * Tests complete flow from Controller to Repository.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Auth Integration Tests")
class AuthIntegrationTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRepository.deleteAll();

        // Create test user with hashed password
        User testUser = TestDataBuilder.buildUser();
        testUser.setName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setIsActive(true);
        userRepository.save(testUser);
    }

    @Test
    @Transactional
    @DisplayName("Integration Test: AUTH Login Flow - Controller to Repository")
    void testLogin_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - Valid login credentials
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "password123");

        // When - Call controller endpoint
        ResponseEntity<AuthResponseDTO> response = authController.login(authRequest);

        // Then - Verify login was successful
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Integration Test: AUTH Login with Invalid Password - Should Fail")
    void testLogin_InvalidPassword_ShouldFail() {
        // Given - Invalid password
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "wrongpassword");

        // When & Then - Should throw AuthenticationException
        assertThatThrownBy(() -> authController.login(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @Transactional
    @DisplayName("Integration Test: AUTH Login with Non-existent User - Should Fail")
    void testLogin_NonExistentUser_ShouldFail() {
        // Given - Non-existent user
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("nonexistent", "password123");

        // When & Then - Should throw AuthenticationException
        assertThatThrownBy(() -> authController.login(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }
}
