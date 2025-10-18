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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testcontainers integration tests for User Management.
 * Tests complete flow from Controller to Repository using real PostgreSQL in Docker.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = false)
@ActiveProfiles("testcontainers")
@DisplayName("User TestContainers Integration Tests")
class UserTestContainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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

        // Create default roles for testcontainers tests
        createDefaultRolesSafely();
    }

    /**
     * Helper method to create default roles for testcontainers tests.
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
    @DisplayName("TestContainers: GET User Flow - Controller to Real PostgresSQL")
    void testGetUser_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - Create user in real PostgreSQL
        User user = TestDataBuilder.buildUser();
        user.setName("testcontainersUser");
        user.setEmail("testcontainers@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.getUserById(savedUser.getId());

        // Then - Verify complete flow with real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("testcontainersUser");
        assertThat(response.getBody().getEmail()).isEqualTo("testcontainers@example.com");
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("TestContainers: CREATE User Flow - Controller to Real PostgreSQL")
    void testCreateUser_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - User creation data
        UserCreateDTO createDTO = TestDataBuilder.buildUserCreateDTO();
        createDTO.setName("newTestcontainersUser");
        createDTO.setEmail("newtestcontainers@example.com");

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.createUser(createDTO);

        // Then - Verify user was created in real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("newTestcontainersUser");

        // Verify user exists in real PostgreSQL repository
        UUID createdUserId = response.getBody().getId();
        User savedUser = userRepository.findByIdAndIsActiveTrue(createdUserId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("newTestcontainersUser");
        assertThat(savedUser.getEmail()).isEqualTo("newtestcontainers@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("TestContainers: UPDATE User Flow - Controller to Real PostgreSQL")
    void testUpdateUser_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - Create user in real PostgreSQL
        User user = TestDataBuilder.buildUser();
        user.setName("originalTestcontainersUser");
        user.setEmail("originaltestcontainers@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // Prepare update data
        UserCreateDTO updateDTO = TestDataBuilder.buildUserCreateDTO();
        updateDTO.setName("updatedTestcontainersUser");
        updateDTO.setEmail("updatedtestcontainers@example.com");

        // When - Call controller endpoint
        ResponseEntity<UserResponseDTO> response = userController.updateUser(savedUser.getId(), updateDTO);

        // Then - Verify user was updated in real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("updatedTestcontainersUser");
        assertThat(response.getBody().getEmail()).isEqualTo("updatedtestcontainers@example.com");

        // Verify changes in real PostgreSQL repository
        User updatedUser = userRepository.findByIdAndIsActiveTrue(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("updatedTestcontainersUser");
        assertThat(updatedUser.getEmail()).isEqualTo("updatedtestcontainers@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("TestContainers: DELETE User Flow - Controller to Real PostgreSQL")
    void testDeleteUser_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - Create user in real PostgreSQL
        User user = TestDataBuilder.buildUser();
        user.setName("tobedeletedTestcontainers");
        user.setEmail("deletetestcontainers@example.com");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // Verify user exists before deletion
        assertThat(userRepository.findByIdAndIsActiveTrue(savedUser.getId())).isPresent();

        // When - Call controller endpoint
        ResponseEntity<Void> response = userController.deleteUser(savedUser.getId());

        // Then - Verify user was soft deleted in real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify user is soft deleted (isActive = false) in real PostgreSQL
        assertThat(userRepository.findByIdAndIsActiveTrue(savedUser.getId())).isEmpty();

        // Verify user still exists in real PostgreSQL database but is inactive
        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.getIsActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @DisplayName("Testcontainers: GET All Users Flow - Controller to Real PostgreSQL")
    void testGetAllUsers_WithRealPostgreSQL_ShouldWorkEndToEnd() {
        // Given - Create multiple users in real PostgreSQL
        User user1 = TestDataBuilder.buildUser();
        user1.setName("testcontainersUser1");
        user1.setEmail("testcontainers1@example.com");
        user1.setIsActive(true);
        userRepository.save(user1);

        User user2 = TestDataBuilder.buildUser();
        user2.setName("testcontainersUser2");
        user2.setEmail("testcontainers2@example.com");
        user2.setIsActive(true);
        userRepository.save(user2);

        // When - Call controller endpoint
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Then - Verify all users are returned from real PostgreSQL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        // Verify usernames are in the response
        List<String> userNames = response.getBody().stream()
                .map(UserResponseDTO::getName)
                .toList();
        assertThat(userNames).containsExactlyInAnyOrder("testcontainersUser1", "testcontainersUser2");
    }
}
