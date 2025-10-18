package com.i2i.usermanagement.security;

import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security tests for JWT token validation.
 * Tests JWT vulnerabilities, edge cases, and security boundaries.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Security Tests")
class JwtSecurityTest {

    private JwtUtil jwtUtil;
    private User testUser;
    private Role testRole;
    private UserRole testUserRole;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Set test JWT secret and expiration
        ReflectionTestUtils.setField(jwtUtil, "secret", "dGVzdFNlY3JldEtleUZvckpXVEVuY29kaW5nVGVzdGluZ1B1cnBvc2VzT25seQ==");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour

        // Create test user with role
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("testuser")
                .email("test@example.com")
                .age(25)
                .password("hashedPassword")
                .isActive(true)
                .build();

        testRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ROLE_USER")
                .build();

        testUserRole = UserRole.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .role(testRole)
                .build();

        testUser.setUserRoles(Arrays.asList(testUserRole));
    }

    @Test
    @DisplayName("Should reject expired JWT token")
    void testJwtToken_ExpiredToken_ShouldBeRejected() {
        // Given - Create expired token
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // Negative expiration
        String expiredToken = jwtUtil.generateToken(testUser);

        // When & Then
        assertThat(jwtUtil.isTokenValid(expiredToken)).isFalse();
        assertThat(jwtUtil.extractUsername(expiredToken)).isNull();
        assertThat(jwtUtil.extractRoles(expiredToken)).isEmpty();
    }

    @Test
    @DisplayName("Should reject JWT token with wrong secret")
    void testJwtToken_WrongSecret_ShouldBeRejected() {
        // Given - Generate token with correct secret
        String validToken = jwtUtil.generateToken(testUser);

        // When - Change secret and try to validate
        ReflectionTestUtils.setField(jwtUtil, "secret", "d3JvbmdTZWNyZXRLZXlGb3JKV1RFbmNvZGluZ1Rlc3RpbmdQdXJwb3Nlc09ubHk=");

        // Then
        assertThat(jwtUtil.isTokenValid(validToken)).isFalse();
        assertThat(jwtUtil.extractUsername(validToken)).isNull();
        assertThat(jwtUtil.extractRoles(validToken)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid.token.here",
        "not-a-jwt-token",
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature",
        "single-part-token",
        "two.parts.only"
    })
    @DisplayName("Should reject malformed JWT tokens")
    void testJwtToken_MalformedTokens_ShouldBeRejected(String malformedToken) {
        // When & Then
        assertThat(jwtUtil.isTokenValid(malformedToken)).isFalse();
        assertThat(jwtUtil.extractUsername(malformedToken)).isNull();
        assertThat(jwtUtil.extractRoles(malformedToken)).isEmpty();
    }

    @Test
    @DisplayName("Should reject JWT token with tampered payload")
    void testJwtToken_TamperedPayload_ShouldBeRejected() {
        // Given - Generate valid token
        String validToken = jwtUtil.generateToken(testUser);

        // When - Tamper with payload (change username in token)
        String[] tokenParts = validToken.split("\\.");
        String tamperedPayload = "eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzQ1Njc4OTAsImV4cCI6MTYzNDU3MTQ5MH0";
        String tamperedToken = tokenParts[0] + "." + tamperedPayload + "." + tokenParts[2];

        // Then
        assertThat(jwtUtil.isTokenValid(tamperedToken)).isFalse();
        assertThat(jwtUtil.extractUsername(tamperedToken)).isNull();
        assertThat(jwtUtil.extractRoles(tamperedToken)).isEmpty();
    }

    @Test
    @DisplayName("Should reject JWT token with tampered signature")
    void testJwtToken_TamperedSignature_ShouldBeRejected() {
        // Given - Generate valid token
        String validToken = jwtUtil.generateToken(testUser);

        // When - Tamper with signature
        String[] tokenParts = validToken.split("\\.");
        String tamperedSignature = "tamperedSignature123456789";
        String tamperedToken = tokenParts[0] + "." + tokenParts[1] + "." + tamperedSignature;

        // Then
        assertThat(jwtUtil.isTokenValid(tamperedToken)).isFalse();
        assertThat(jwtUtil.extractUsername(tamperedToken)).isNull();
        assertThat(jwtUtil.extractRoles(tamperedToken)).isEmpty();
    }

    @Test
    @DisplayName("Should reject JWT token with missing required claims")
    void testJwtToken_MissingClaims_ShouldBeRejected() {
        // Given - Token without subject claim
        String tokenWithoutSubject = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNjM0NTY3ODkwLCJleHAiOjE2MzQ1NzE0OTB9.signature";

        // When & Then
        assertThat(jwtUtil.isTokenValid(tokenWithoutSubject)).isFalse();
        assertThat(jwtUtil.extractUsername(tokenWithoutSubject)).isNull();
    }

    @Test
    @DisplayName("Should reject JWT token with invalid algorithm")
    void testJwtToken_InvalidAlgorithm_ShouldBeRejected() {
        // Given - Token with different algorithm
        String tokenWithDifferentAlg = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzQ1Njc4OTAsImV4cCI6MTYzNDU3MTQ5MH0.signature";

        // When & Then
        assertThat(jwtUtil.isTokenValid(tokenWithDifferentAlg)).isFalse();
        assertThat(jwtUtil.extractUsername(tokenWithDifferentAlg)).isNull();
        assertThat(jwtUtil.extractRoles(tokenWithDifferentAlg)).isEmpty();
    }

    @Test
    @DisplayName("Should handle JWT token with empty roles gracefully")
    void testJwtToken_EmptyRoles_ShouldHandleGracefully() {
        // Given - User with no roles
        User userWithoutRoles = User.builder()
                .id(UUID.randomUUID())
                .name("testuser")
                .email("test@example.com")
                .age(25)
                .password("hashedPassword")
                .isActive(true)
                .userRoles(Arrays.asList()) // Empty roles
                .build();

        // When
        String token = jwtUtil.generateToken(userWithoutRoles);

        // Then
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.extractRoles(token)).isEmpty();
    }

    @Test
    @DisplayName("Should reject JWT token with future issued time")
    void testJwtToken_FutureIssuedTime_ShouldBeRejected() {
        // Given - Token with future iat (issued at)
        long futureTime = System.currentTimeMillis() + 3600000; // 1 hour in future
        String futureToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzQ1Njc4OTAsImV4cCI6" + futureTime + "fQ.signature";

        // When & Then
        assertThat(jwtUtil.isTokenValid(futureToken)).isFalse();
        assertThat(jwtUtil.extractUsername(futureToken)).isNull();
    }

    @Test
    @DisplayName("Should handle JWT token with very long username")
    void testJwtToken_VeryLongUsername_ShouldHandleGracefully() {
        // Given - User with very long username
        String longUsername = "a".repeat(1000);
        User userWithLongName = User.builder()
                .id(UUID.randomUUID())
                .name(longUsername)
                .email("test@example.com")
                .age(25)
                .password("hashedPassword")
                .isActive(true)
                .userRoles(Arrays.asList(testUserRole))
                .build();

        // When
        String token = jwtUtil.generateToken(userWithLongName);

        // Then
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(longUsername);
    }

    @Test
    @DisplayName("Should handle JWT token with special characters in username")
    void testJwtToken_SpecialCharactersInUsername_ShouldHandleGracefully() {
        // Given - User with special characters in username
        String specialUsername = "test@user#123$%^&*()";
        User userWithSpecialName = User.builder()
                .id(UUID.randomUUID())
                .name(specialUsername)
                .email("test@example.com")
                .age(25)
                .password("hashedPassword")
                .isActive(true)
                .userRoles(Arrays.asList(testUserRole))
                .build();

        // When
        String token = jwtUtil.generateToken(userWithSpecialName);

        // Then
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(specialUsername);
    }

    @Test
    @DisplayName("Should handle JWT token with unicode characters")
    void testJwtToken_UnicodeCharacters_ShouldHandleGracefully() {
        // Given - User with unicode characters
        String unicodeUsername = "测试用户123";
        User userWithUnicodeName = User.builder()
                .id(UUID.randomUUID())
                .name(unicodeUsername)
                .email("test@example.com")
                .age(25)
                .password("hashedPassword")
                .isActive(true)
                .userRoles(Arrays.asList(testUserRole))
                .build();

        // When
        String token = jwtUtil.generateToken(userWithUnicodeName);

        // Then
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(unicodeUsername);
    }

    @Test
    @DisplayName("Should reject JWT token with negative expiration")
    void testJwtToken_NegativeExpiration_ShouldBeRejected() {
        // Given - Token with negative expiration
        String negativeExpToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzQ1Njc4OTAsImV4cCI6LTF9.signature";

        // When & Then
        assertThat(jwtUtil.isTokenValid(negativeExpToken)).isFalse();
        assertThat(jwtUtil.extractUsername(negativeExpToken)).isNull();
    }

    @Test
    @DisplayName("Should handle JWT token with multiple roles correctly")
    void testJwtToken_MultipleRoles_ShouldHandleCorrectly() {
        // Given - User with multiple roles
        Role adminRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ROLE_ADMIN")
                .build();

        UserRole adminUserRole = UserRole.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .role(adminRole)
                .build();

        testUser.setUserRoles(Arrays.asList(testUserRole, adminUserRole));

        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        List<String> roles = jwtUtil.extractRoles(token);
        assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}
