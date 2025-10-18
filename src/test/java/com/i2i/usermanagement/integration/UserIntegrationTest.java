package com.i2i.usermanagement.integration;

import com.i2i.usermanagement.controller.UserController;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.repository.RoleRepository;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.repository.UserRoleRepository;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for User Management.
 * Tests complete flow from Controller to Repository.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User Integration Tests")
class UserIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create default roles for integration tests
        // Use a more robust approach to prevent unique constraint violations
        createDefaultRolesSafely();
    }

    /**
     * Helper method to create default roles for integration tests.
     * Uses a more robust approach to prevent unique constraint violations.
     */
    private void createDefaultRolesSafely() {
        // Check if ROLE_USER exists before creating
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .build();
            roleRepository.save(userRole);
        }

        // Check if ROLE_ADMIN exists before creating
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .build();
            roleRepository.save(adminRole);
        }
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("Integration Test: GET User Flow - Controller to Repository")
    void testGetUser_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - Create user without role for simpler testing
        User user = TestDataBuilder.buildUser();
        user.setName("integrationUser");
        user.setEmail("integration@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.getUserById(savedUser.getId());

        // Then - Verify complete flow
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("integrationUser");
        assertThat(response.getBody().getEmail()).isEqualTo("integration@example.com");
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("Integration Test: CREATE User Flow - Controller to Repository")
    void testCreateUser_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - User creation data
        UserCreateDTO createDTO = TestDataBuilder.buildUserCreateDTO();
        createDTO.setName("newUser");
        createDTO.setEmail("newuser@example.com");

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.createUser(createDTO);

        // Then - Verify user was created in database
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("newUser");
        // Note: createUser only returns id and name, not email

        // Verify user exists in repository
        UUID createdUserId = response.getBody().getId();
        User savedUser = userRepository.findByIdAndIsActiveTrue(createdUserId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("newUser");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("Integration Test: UPDATE User Flow - Controller to Repository")
    void testUpdateUser_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - Create user without role for simpler testing
        User user = TestDataBuilder.buildUser();
        user.setName("originalUser");
        user.setEmail("original@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // Prepare update data
        UserCreateDTO updateDTO = TestDataBuilder.buildUserCreateDTO();
        updateDTO.setName("updatedUser");
        updateDTO.setEmail("updated@example.com");

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.updateUser(savedUser.getId(), updateDTO);

        // Then - Verify user was updated in database
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("updatedUser");
        assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");

        // Verify changes in repository
        User updatedUser = userRepository.findByIdAndIsActiveTrue(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("updatedUser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback(false)
    @DisplayName("Integration Test: DELETE User Flow - Controller to Repository")
    void testDeleteUser_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - Create user without role for simpler testing
        User user = TestDataBuilder.buildUser();
        user.setName("tobedeleted");
        user.setEmail("delete@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // Verify user exists before deletion
        assertThat(userRepository.findByIdAndIsActiveTrue(savedUser.getId())).isPresent();

        // When - Call controller endpoint
        ResponseEntity<Void> response = userController.deleteUser(savedUser.getId());

        // Then - Verify user was soft deleted
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify user is soft deleted (isActive = false) - use findByIdAndIsActiveTrue
        assertThat(userRepository.findByIdAndIsActiveTrue(savedUser.getId())).isEmpty();

        // Verify user still exists in database but is inactive - use findById
        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.getIsActive()).isFalse();

        // Additional verification: check that the user is not found in active users
        assertThat(userRepository.findByIdAndIsActiveTrue(savedUser.getId())).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("Integration Test: GET All Users Flow - Controller to Repository")
    void testGetAllUsers_IntegrationFlow_ShouldWorkEndToEnd() {
        // Given - Create multiple users without roles for simpler testing
        User user1 = TestDataBuilder.buildUser();
        user1.setName("user1");
        user1.setEmail("user1@example.com");
        user1.setIsActive(true);
        userRepository.save(user1);

        User user2 = TestDataBuilder.buildUser();
        user2.setName("user2");
        user2.setEmail("user2@example.com");
        user2.setIsActive(true);
        userRepository.save(user2);

        // When - Call controller endpoint
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Then - Verify all users are returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        // Verify usernames are in the response
        List<String> userNames = response.getBody().stream()
                .map(UserResponseDTO::getName)
                .toList();
        assertThat(userNames).containsExactlyInAnyOrder("user1", "user2");
    }
}
