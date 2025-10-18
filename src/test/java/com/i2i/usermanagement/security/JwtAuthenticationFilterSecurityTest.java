package com.i2i.usermanagement.security;

import com.i2i.usermanagement.filter.JwtAuthenticationFilter;
import com.i2i.usermanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Security tests for JWT Authentication Filter.
 * Tests filter security vulnerabilities and edge cases.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Security Tests")
class JwtAuthenticationFilterSecurityTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should reject request with malformed Authorization header")
    void testFilter_MalformedAuthHeader_ShouldReject() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should reject request with empty Bearer token")
    void testFilter_EmptyBearerToken_ShouldReject() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        when(jwtUtil.extractUsername("")).thenReturn(null);
        // Note: isTokenValid() won't be called because extractUsername() returns null (short-circuit)

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(""); // This WILL be called because "Bearer " passes startsWith check
        verify(jwtUtil, never()).isTokenValid(""); // Should NOT be called due to short-circuit
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should reject request with null Authorization header")
    void testFilter_NullAuthHeader_ShouldReject() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should reject request with invalid JWT token")
    void testFilter_InvalidJwtToken_ShouldReject() throws ServletException, IOException {
        // Given
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.extractUsername(invalidToken)).thenReturn(null);
        // Note: isTokenValid() won't be called because extractUsername() returns null (short-circuit)

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(invalidToken);
        verify(jwtUtil, never()).isTokenValid(invalidToken); // Should NOT be called due to short-circuit
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should reject request with expired JWT token")
    void testFilter_ExpiredJwtToken_ShouldReject() throws ServletException, IOException {
        // Given
        String expiredToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtUtil.extractUsername(expiredToken)).thenReturn("testuser");
        when(jwtUtil.isTokenValid(expiredToken)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(expiredToken);
        verify(jwtUtil).isTokenValid(expiredToken); // This WILL be called because username is not null
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should reject request with tampered JWT token")
    void testFilter_TamperedJwtToken_ShouldReject() throws ServletException, IOException {
        // Given
        String tamperedToken = "tampered.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tamperedToken);
        when(jwtUtil.extractUsername(tamperedToken)).thenReturn(null);
        // Note: isTokenValid() won't be called because extractUsername() returns null (short-circuit)

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(tamperedToken);
        verify(jwtUtil, never()).isTokenValid(tamperedToken); // Should NOT be called due to short-circuit
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should handle request with valid JWT token correctly")
    void testFilter_ValidJwtToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Given
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn("testuser");
        when(jwtUtil.isTokenValid(validToken)).thenReturn(true);
        when(jwtUtil.extractRoles(validToken)).thenReturn(java.util.Arrays.asList("ROLE_USER"));

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(validToken);
        verify(jwtUtil).isTokenValid(validToken);
        verify(jwtUtil).extractRoles(validToken);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should handle request with very long Authorization header")
    void testFilter_VeryLongAuthHeader_ShouldHandleGracefully() throws ServletException, IOException {
        // Given
        String longToken = "a".repeat(10000);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + longToken);
        when(jwtUtil.extractUsername(longToken)).thenReturn(null);
        // Note: isTokenValid() won't be called because extractUsername() returns null (short-circuit)

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(longToken);
        verify(jwtUtil, never()).isTokenValid(longToken); // Should NOT be called due to short-circuit
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should handle request with special characters in token")
    void testFilter_SpecialCharactersInToken_ShouldHandleGracefully() throws ServletException, IOException {
        // Given
        String specialToken = "special@#$%^&*()_+{}|:<>?[]\\;'\",./";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + specialToken);
        when(jwtUtil.extractUsername(specialToken)).thenReturn(null);
        // Note: isTokenValid() won't be called because extractUsername() returns null (short-circuit)

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername(specialToken);
        verify(jwtUtil, never()).isTokenValid(specialToken); // Should NOT be called due to short-circuit
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
