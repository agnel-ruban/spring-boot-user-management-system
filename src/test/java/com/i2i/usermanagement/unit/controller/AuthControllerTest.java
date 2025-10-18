package com.i2i.usermanagement.unit.controller;

import com.i2i.usermanagement.controller.AuthController;
import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.dto.AuthResponseDTO;
import com.i2i.usermanagement.service.AuthService;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthController class.
 * Tests authentication REST endpoints with proper mocking.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthRequestDTO testAuthRequestDTO;
    private AuthResponseDTO testAuthResponseDTO;

    @BeforeEach
    void setUp() {
        testAuthRequestDTO = TestDataBuilder.buildAuthRequestDTO();
        testAuthResponseDTO = AuthResponseDTO.builder()
                .token("valid.jwt.token")
                .type("Bearer")
                .build();
    }

    @Test
    @DisplayName("Should authenticate user successfully and return JWT token")
    void testLogin_ValidCredentials_ShouldReturnAuthResponse() {
        // Given
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(testAuthRequestDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getBody().getType()).isEqualTo("Bearer");

        // Verify service method was called
        verify(authService).authenticate(testAuthRequestDTO);
    }

    @Test
    @DisplayName("Should handle authentication with different token types")
    void testLogin_DifferentTokenTypes_ShouldReturnAuthResponse() {
        // Given
        AuthResponseDTO customAuthResponse = AuthResponseDTO.builder()
                .token("custom.jwt.token")
                .type("JWT")
                .build();

        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(customAuthResponse);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(testAuthRequestDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("custom.jwt.token");
        assertThat(response.getBody().getType()).isEqualTo("JWT");

        // Verify service method was called
        verify(authService).authenticate(testAuthRequestDTO);
    }

    @Test
    @DisplayName("Should handle authentication with long JWT token")
    void testLogin_LongJwtToken_ShouldReturnAuthResponse() {
        // Given
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        AuthResponseDTO longTokenResponse = AuthResponseDTO.builder()
                .token(longToken)
                .type("Bearer")
                .build();

        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(longTokenResponse);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(testAuthRequestDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(longToken);
        assertThat(response.getBody().getType()).isEqualTo("Bearer");

        // Verify service method was called
        verify(authService).authenticate(testAuthRequestDTO);
    }

    @Test
    @DisplayName("Should handle authentication with empty username")
    void testLogin_EmptyUsername_ShouldPassToService() {
        // Given
        AuthRequestDTO emptyUsernameRequest = TestDataBuilder.buildAuthRequestDTO("", "password123");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(emptyUsernameRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with empty username
        verify(authService).authenticate(emptyUsernameRequest);
    }

    @Test
    @DisplayName("Should handle authentication with empty password")
    void testLogin_EmptyPassword_ShouldPassToService() {
        // Given
        AuthRequestDTO emptyPasswordRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(emptyPasswordRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with empty password
        verify(authService).authenticate(emptyPasswordRequest);
    }

    @Test
    @DisplayName("Should handle authentication with null username")
    void testLogin_NullUsername_ShouldPassToService() {
        // Given
        AuthRequestDTO nullUsernameRequest = TestDataBuilder.buildAuthRequestDTO(null, "password123");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(nullUsernameRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with null username
        verify(authService).authenticate(nullUsernameRequest);
    }

    @Test
    @DisplayName("Should handle authentication with null password")
    void testLogin_NullPassword_ShouldPassToService() {
        // Given
        AuthRequestDTO nullPasswordRequest = TestDataBuilder.buildAuthRequestDTO("testuser", null);
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(nullPasswordRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with null password
        verify(authService).authenticate(nullPasswordRequest);
    }

    @Test
    @DisplayName("Should handle authentication with null request")
    void testLogin_NullRequest_ShouldPassToService() {
        // Given
        when(authService.authenticate(null))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with null request
        verify(authService).authenticate(null);
    }

    @Test
    @DisplayName("Should handle authentication with special characters in username")
    void testLogin_SpecialCharactersInUsername_ShouldPassToService() {
        // Given
        AuthRequestDTO specialCharRequest = TestDataBuilder.buildAuthRequestDTO("user@domain.com", "password123");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(specialCharRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with special characters
        verify(authService).authenticate(specialCharRequest);
    }

    @Test
    @DisplayName("Should handle authentication with special characters in password")
    void testLogin_SpecialCharactersInPassword_ShouldPassToService() {
        // Given
        AuthRequestDTO specialCharRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "p@ssw0rd!@#$%");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(specialCharRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with special characters
        verify(authService).authenticate(specialCharRequest);
    }

    @Test
    @DisplayName("Should handle authentication with very long username")
    void testLogin_VeryLongUsername_ShouldPassToService() {
        // Given
        String longUsername = "a".repeat(1000); // Very long username
        AuthRequestDTO longUsernameRequest = TestDataBuilder.buildAuthRequestDTO(longUsername, "password123");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(longUsernameRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with long username
        verify(authService).authenticate(longUsernameRequest);
    }

    @Test
    @DisplayName("Should handle authentication with very long password")
    void testLogin_VeryLongPassword_ShouldPassToService() {
        // Given
        String longPassword = "p".repeat(1000); // Very long password
        AuthRequestDTO longPasswordRequest = TestDataBuilder.buildAuthRequestDTO("testuser", longPassword);
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(longPasswordRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with long password
        verify(authService).authenticate(longPasswordRequest);
    }

    @Test
    @DisplayName("Should handle authentication with whitespace in username")
    void testLogin_WhitespaceInUsername_ShouldPassToService() {
        // Given
        AuthRequestDTO whitespaceRequest = TestDataBuilder.buildAuthRequestDTO("  testuser  ", "password123");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(whitespaceRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with whitespace
        verify(authService).authenticate(whitespaceRequest);
    }

    @Test
    @DisplayName("Should handle authentication with whitespace in password")
    void testLogin_WhitespaceInPassword_ShouldPassToService() {
        // Given
        AuthRequestDTO whitespaceRequest = TestDataBuilder.buildAuthRequestDTO("testuser", "  password123  ");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(whitespaceRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with whitespace
        verify(authService).authenticate(whitespaceRequest);
    }

    @Test
    @DisplayName("Should handle authentication with unicode characters")
    void testLogin_UnicodeCharacters_ShouldPassToService() {
        // Given
        AuthRequestDTO unicodeRequest = TestDataBuilder.buildAuthRequestDTO("用户123", "密码456");
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(testAuthResponseDTO);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(unicodeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with unicode characters
        verify(authService).authenticate(unicodeRequest);
    }
}
