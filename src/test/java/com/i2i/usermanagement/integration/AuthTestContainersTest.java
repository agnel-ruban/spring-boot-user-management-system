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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testcontainers integration tests for Authentication.
 * Tests complete flow from Controller to Repository using real PostgreSQL in Docker.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = false)
@ActiveProfiles("testcontainers")
@DisplayName("Auth TestContainers Integration Tests")
class AuthTestContainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("***")
            .withUsername("***")
            .withPassword("****")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRepository.deleteAll();

        // Create test user in real PostgreSQL
        testUser = TestDataBuilder.buildUser();
        testUser.setName("testcontainersuser");
        testUser.setEmail("testcontainers@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setIsActive(true);
        userRepository.save(testUser);
    }

    @Test
    @Transactional
    @DisplayName("Testcontainers: AUTH Login Flow - Controller to Real PostgreSQL")
    void testLogin_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - Valid login credentials
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testcontainersuser", "password123");

        // When - Call controller endpoint
        ResponseEntity<AuthResponseDTO> response = authController.login(authRequest);

        // Then - Verify login was successful with real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Testcontainers: AUTH Login with Invalid Password - Should Fail")
    void testLogin_InvalidPassword_WithRealPostgreSQL_ShouldFail() {
        // Given - Invalid password
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testcontainersuser", "wrongpassword");

        // When & Then - Should throw AuthenticationException
        assertThatThrownBy(() -> authController.login(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @Transactional
    @DisplayName("Testcontainers: AUTH Login with Non-existent User - Should Fail")
    void testLogin_NonExistentUser_WithRealPostgreSQL_ShouldFail() {
        // Given - Non-existent user
        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("nonexistent", "password123");

        // When & Then - Should throw AuthenticationException
        assertThatThrownBy(() -> authController.login(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }
}
