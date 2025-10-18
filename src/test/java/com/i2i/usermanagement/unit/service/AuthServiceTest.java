package com.i2i.usermanagement.unit.service;

import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.dto.AuthResponseDTO;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.exception.AuthenticationException;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.service.AuthService;
import com.i2i.usermanagement.service.JwtService;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthService class.
 * Tests authentication logic, password verification, and JWT token generation.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

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
        // Clear SecurityContext before each test
        SecurityContextHolder.clearContext();

        // Create test role
        testRole = TestDataBuilder.buildRole("ROLE_USER");

        // Create test user
        testUser = TestDataBuilder.buildUser();
        testUser.setName("testuser");

        // Create user role mapping
        testUserRole = TestDataBuilder.buildUserRole(testUser, testRole);
        testUser.setUserRoles(Arrays.asList(testUserRole));

        // Create valid auth request
        validAuthRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "password123");
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials and generate JWT token")
    void testAuthenticate_ValidCredentials_ShouldReturnAuthResponse() {
        // Given
        String expectedToken = "valid.jwt.token";

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.authenticate(validAuthRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getType()).isEqualTo("Bearer");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should throw AuthenticationException when user not found")
    void testAuthenticate_UserNotFound_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue("nonexistentuser"))
                .thenReturn(Optional.empty());

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("nonexistentuser", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("nonexistentuser");
    }

    @Test
    @DisplayName("Should throw AuthenticationException when password is incorrect")
    void testAuthenticate_IncorrectPassword_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword()))
                .thenReturn(false);

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "wrongpassword");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("wrongpassword", testUser.getPassword());
    }

    @Test
    @DisplayName("Should throw AuthenticationException when user is inactive")
    void testAuthenticate_InactiveUser_ShouldThrowAuthenticationException() {
        // Given
        User inactiveUser = TestDataBuilder.buildUser();
        inactiveUser.setName("inactiveuser");
        inactiveUser.setIsActive(false);

        when(userRepository.findByNameAndIsActiveTrue("inactiveuser"))
                .thenReturn(Optional.empty()); // Inactive users are not returned by the query

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("inactiveuser", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("inactiveuser");
    }

    @Test
    @DisplayName("Should authenticate user with admin role and generate JWT token")
    void testAuthenticate_AdminUser_ShouldReturnAuthResponse() {
        // Given
        Role adminRole = TestDataBuilder.buildRole("ROLE_ADMIN");
        UserRole adminUserRole = TestDataBuilder.buildUserRole(testUser, adminRole);
        testUser.setUserRoles(Arrays.asList(adminUserRole));

        String expectedToken = "admin.jwt.token";

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.authenticate(validAuthRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getType()).isEqualTo("Bearer");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should authenticate user with multiple roles and generate JWT token")
    void testAuthenticate_UserWithMultipleRoles_ShouldReturnAuthResponse() {
        // Given
        Role userRole = TestDataBuilder.buildRole("ROLE_USER");
        Role adminRole = TestDataBuilder.buildRole("ROLE_ADMIN");

        UserRole userUserRole = TestDataBuilder.buildUserRole(testUser, userRole);
        UserRole adminUserRole = TestDataBuilder.buildUserRole(testUser, adminRole);
        testUser.setUserRoles(Arrays.asList(userUserRole, adminUserRole));

        String expectedToken = "multirole.jwt.token";

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.authenticate(validAuthRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getType()).isEqualTo("Bearer");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void testAuthenticate_UserWithNoRoles_ShouldReturnAuthResponse() {
        // Given
        testUser.setUserRoles(List.of()); // No roles

        String expectedToken = "no.roles.jwt.token";

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.authenticate(validAuthRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getType()).isEqualTo("Bearer");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should handle user with null roles")
    void testAuthenticate_UserWithNullRoles_ShouldReturnAuthResponse() {
        // Given
        testUser.setUserRoles(null); // Null roles

        String expectedToken = "null.roles.jwt.token";

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.authenticate(validAuthRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getType()).isEqualTo("Bearer");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should handle empty username")
    void testAuthenticate_EmptyUsername_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue(""))
                .thenReturn(Optional.empty());

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("");
    }

    @Test
    @DisplayName("Should handle empty password")
    void testAuthenticate_EmptyPassword_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("", testUser.getPassword()))
                .thenReturn(false);

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches("", testUser.getPassword());
    }

    @Test
    @DisplayName("Should handle null username")
    void testAuthenticate_NullUsername_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue(null))
                .thenReturn(Optional.empty());

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO(null, "password123");

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue(null);
    }

    @Test
    @DisplayName("Should handle null password")
    void testAuthenticate_NullPassword_ShouldThrowAuthenticationException() {
        // Given
        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(null, testUser.getPassword()))
                .thenReturn(false);

        AuthRequestDTO authRequest = TestDataBuilder.buildAuthRequestDTO("testuser", null);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        // Verify SecurityContext is not set
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(passwordEncoder).matches(null, testUser.getPassword());
    }
}
