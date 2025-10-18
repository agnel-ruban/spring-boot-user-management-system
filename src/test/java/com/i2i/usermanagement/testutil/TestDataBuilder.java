package com.i2i.usermanagement.testutil;

import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.AuthRequestDTO;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Test data builder utility class for creating test objects.
 * Provides static methods to create test data with default values.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
public class TestDataBuilder {

    // Default test values
    private static final String DEFAULT_NAME = "John Doe";
    private static final String DEFAULT_EMAIL = "john.doe@example.com";
    private static final Integer DEFAULT_AGE = 25;
    private static final String DEFAULT_PHONE = "+1234567890";
    private static final String DEFAULT_ADDRESS = "123 Main St, City, Country";
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String DEFAULT_USERNAME = "johndoe";
    private static final String DEFAULT_ROLE_NAME = "ROLE_USER";
    private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

    /**
     * Creates a UserCreateDTO with default test values.
     * 
     * @return UserCreateDTO with default values
     */
    public static UserCreateDTO buildUserCreateDTO() {
        return UserCreateDTO.builder()
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .age(DEFAULT_AGE)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .password(DEFAULT_PASSWORD)
                .build();
    }

    /**
     * Creates a UserCreateDTO with custom values.
     * 
     * @param name the name
     * @param email the email
     * @param age the age
     * @return UserCreateDTO with custom values
     */
    public static UserCreateDTO buildUserCreateDTO(String name, String email, Integer age) {
        return UserCreateDTO.builder()
                .name(name)
                .email(email)
                .age(age)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .password(DEFAULT_PASSWORD)
                .build();
    }

    /**
     * Creates a UserUpdateDTO with default test values.
     * 
     * @return UserUpdateDTO with default values
     */
    public static UserUpdateDTO buildUserUpdateDTO() {
        return UserUpdateDTO.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .age(30)
                .phoneNumber("+9876543210")
                .address("456 Updated St, City, Country")
                .password("newpassword123")
                .isActive(true)
                .build();
    }

    /**
     * Creates a UserUpdateDTO with only specific fields.
     * 
     * @param name the name (can be null)
     * @param email the email (can be null)
     * @return UserUpdateDTO with partial values
     */
    public static UserUpdateDTO buildPartialUserUpdateDTO(String name, String email) {
        return UserUpdateDTO.builder()
                .name(name)
                .email(email)
                .build();
    }

    /**
     * Creates a UserResponseDTO with default test values.
     * 
     * @return UserResponseDTO with default values
     */
    public static UserResponseDTO buildUserResponseDTO() {
        return UserResponseDTO.builder()
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .age(DEFAULT_AGE)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .isActive(true)
                .build();
    }

    /**
     * Creates a UserResponseDTO with custom values.
     * 
     * @param name the name
     * @param email the email
     * @return UserResponseDTO with custom values
     */
    public static UserResponseDTO buildUserResponseDTO(String name, String email) {
        return UserResponseDTO.builder()
                .name(name)
                .email(email)
                .age(DEFAULT_AGE)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .isActive(true)
                .build();
    }

    /**
     * Creates an AuthRequestDTO with default test values.
     * 
     * @return AuthRequestDTO with default values
     */
    public static AuthRequestDTO buildAuthRequestDTO() {
        return AuthRequestDTO.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .build();
    }

    /**
     * Creates an AuthRequestDTO with custom values.
     * 
     * @param username the username
     * @param password the password
     * @return AuthRequestDTO with custom values
     */
    public static AuthRequestDTO buildAuthRequestDTO(String username, String password) {
        return AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Creates a User entity with default test values.
     * 
     * @return User entity with default values
     */
    public static User buildUser() {
        return User.builder()
                .name(DEFAULT_NAME)
                .email(DEFAULT_EMAIL)
                .age(DEFAULT_AGE)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .password("hashedPassword123")
                .isActive(true)
                .build();
    }

    /**
     * Creates a User entity with custom values.
     * 
     * @param name the name
     * @param email the email
     * @param isActive the active status
     * @return User entity with custom values
     */
    public static User buildUser(String name, String email, Boolean isActive) {
        return User.builder()
                .name(name)
                .email(email)
                .age(DEFAULT_AGE)
                .phoneNumber(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS)
                .password("hashedPassword123")
                .isActive(isActive)
                .build();
    }

    /**
     * Creates a Role entity with default test values.
     * 
     * @return Role entity with default values
     */
    public static Role buildRole() {
        return Role.builder()
                .name(DEFAULT_ROLE_NAME)
                .build();
    }

    /**
     * Creates a Role entity with custom name.
     * 
     * @param roleName the role name
     * @return Role entity with custom name
     */
    public static Role buildRole(String roleName) {
        return Role.builder()
                .name(roleName)
                .build();
    }

    /**
     * Creates a UserRole entity with default test values.
     * 
     * @param user the user
     * @param role the role
     * @return UserRole entity with default values
     */
    public static UserRole buildUserRole(User user, Role role) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }

    /**
     * Creates a list of test users.
     * 
     * @param count the number of users to create
     * @return List of User entities
     */
    public static List<User> buildUserList(int count) {
        return List.of(
                buildUser("User1", "user1@example.com", true),
                buildUser("User2", "user2@example.com", true),
                buildUser("User3", "user3@example.com", true)
        );
    }

    /**
     * Creates a list of test UserResponseDTOs.
     * 
     * @param count the number of DTOs to create
     * @return List of UserResponseDTO entities
     */
    public static List<UserResponseDTO> buildUserResponseDTOList(int count) {
        return List.of(
                buildUserResponseDTO("User1", "user1@example.com"),
                buildUserResponseDTO("User2", "user2@example.com"),
                buildUserResponseDTO("User3", "user3@example.com")
        );
    }
}
