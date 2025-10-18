package com.i2i.usermanagement.unit.repository;

import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserRepository class.
 * Tests essential repository methods with real database operations.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Unit Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = TestDataBuilder.buildUser();
        testUser.setName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setIsActive(true);

        // Create inactive user
        inactiveUser = TestDataBuilder.buildUser();
        inactiveUser.setName("inactiveuser");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setIsActive(false);

        // Save users to database
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(inactiveUser);
    }

    @Test
    @DisplayName("Should find active user by ID")
    void testFindByIdAndIsActiveTrue_ActiveUserExists_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByIdAndIsActiveTrue(testUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testUser.getId());
        assertThat(result.get().getName()).isEqualTo("testuser");
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should return empty when user is inactive")
    void testFindByIdAndIsActiveTrue_InactiveUserExists_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByIdAndIsActiveTrue(inactiveUser.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when user does not exist")
    void testFindByIdAndIsActiveTrue_UserNotExists_ShouldReturnEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<User> result = userRepository.findByIdAndIsActiveTrue(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find all active users")
    void testFindByIsActiveTrue_ActiveUsersExist_ShouldReturnActiveUsers() {
        // Given
        User anotherActiveUser = TestDataBuilder.buildUser();
        anotherActiveUser.setName("anotheruser");
        anotherActiveUser.setEmail("another@example.com");
        anotherActiveUser.setIsActive(true);
        entityManager.persistAndFlush(anotherActiveUser);

        // When
        List<User> result = userRepository.findByIsActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getIsActive).containsOnly(true);
    }

    @Test
    @DisplayName("Should return empty list when no active users exist")
    void testFindByIsActiveTrue_NoActiveUsers_ShouldReturnEmptyList() {
        // Given
        entityManager.remove(testUser);
        entityManager.flush();

        // When
        List<User> result = userRepository.findByIsActiveTrue();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should check if active user exists by email")
    void testExistsByEmailAndIsActiveTrue_ActiveUserExists_ShouldReturnTrue() {
        // When
        boolean result = userRepository.existsByEmailAndIsActiveTrue("test@example.com");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when user is inactive")
    void testExistsByEmailAndIsActiveTrue_InactiveUserExists_ShouldReturnFalse() {
        // When
        boolean result = userRepository.existsByEmailAndIsActiveTrue("inactive@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void testExistsByEmailAndIsActiveTrue_UserNotExists_ShouldReturnFalse() {
        // When
        boolean result = userRepository.existsByEmailAndIsActiveTrue("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should find active user by name")
    void testFindByNameAndIsActiveTrue_ActiveUserExists_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByNameAndIsActiveTrue("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("testuser");
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should return empty when user is inactive")
    void testFindByNameAndIsActiveTrue_InactiveUserExists_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByNameAndIsActiveTrue("inactiveuser");

        // Then
        assertThat(result).isEmpty();
    }
}
