package com.i2i.usermanagement.security;

import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.exception.AuthenticationException;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.service.AuthService;
import com.i2i.usermanagement.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Security tests for Authentication Service.
 * Tests authentication security vulnerabilities and edge cases.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Security Tests")
class AuthServiceSecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private UserRole testUserRole;
    private AuthRequestDTO validAuthRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(UUID.randomUUID())
            .name("testuser")
            .email("test@example.com")
            .age(25)
            .password("$2a$10$hashedPassword")
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

        validAuthRequest = AuthRequestDTO.builder()
            .username("testuser")
            .password("password123")
            .build();
    }

    @Test
    @DisplayName("Should reject authentication with SQL injection in username")
    void testAuthenticate_SqlInjectionInUsername_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("'; DROP TABLE users; --")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("'; DROP TABLE users; --");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with XSS payload in username")
    void testAuthenticate_XssPayloadInUsername_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("<script>alert('xss')</script>")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("<script>alert('xss')</script>");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with very long username")
    void testAuthenticate_VeryLongUsername_ShouldReject() {
        // Given
        String longUsername = "a".repeat(10000);
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username(longUsername)
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue(longUsername);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with null username")
    void testAuthenticate_NullUsername_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username(null)
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue(null)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue(null);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with empty username")
    void testAuthenticate_EmptyUsername_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with whitespace-only username")
    void testAuthenticate_WhitespaceOnlyUsername_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("   ")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue("   ")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("   ");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with null password")
    void testAuthenticate_NullPassword_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("testuser")
            .password(null)
            .build();

        when(userRepository.findByNameAndIsActiveTrue("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(null, testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches(null, testUser.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with empty password")
    void testAuthenticate_EmptyPassword_ShouldReject() {
        // Given
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("testuser")
            .password("")
            .build();

        when(userRepository.findByNameAndIsActiveTrue("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("", testUser.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with very long password")
    void testAuthenticate_VeryLongPassword_ShouldReject() {
        // Given
        String longPassword = "a".repeat(10000);
        AuthRequestDTO maliciousRequest = AuthRequestDTO.builder()
            .username("testuser")
            .password(longPassword)
            .build();

        when(userRepository.findByNameAndIsActiveTrue("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(longPassword, testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(maliciousRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches(longPassword, testUser.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject authentication with inactive user")
    void testAuthenticate_InactiveUser_ShouldReject() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findByNameAndIsActiveTrue("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(validAuthRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should handle authentication with special characters in username")
    void testAuthenticate_SpecialCharactersInUsername_ShouldHandleGracefully() {
        // Given
        AuthRequestDTO specialRequest = AuthRequestDTO.builder()
            .username("test@user#123$%^&*()")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue("test@user#123$%^&*()")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(specialRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("test@user#123$%^&*()");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should handle authentication with unicode characters in username")
    void testAuthenticate_UnicodeCharactersInUsername_ShouldHandleGracefully() {
        // Given
        AuthRequestDTO unicodeRequest = AuthRequestDTO.builder()
            .username("测试用户123")
            .password("password123")
            .build();

        when(userRepository.findByNameAndIsActiveTrue("测试用户123")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(unicodeRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("Invalid username or password");

        verify(userRepository).findByNameAndIsActiveTrue("测试用户123");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
