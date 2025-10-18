package com.i2i.usermanagement.unit.controller;

import com.i2i.usermanagement.controller.UserController;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;
import com.i2i.usermanagement.service.UserService;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserController class.
 * Tests all REST endpoints with proper mocking.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserCreateDTO testUserCreateDTO;
    private UserUpdateDTO testUserUpdateDTO;
    private UserResponseDTO testUserResponseDTO;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserCreateDTO = TestDataBuilder.buildUserCreateDTO();
        testUserUpdateDTO = TestDataBuilder.buildUserUpdateDTO();
        testUserResponseDTO = TestDataBuilder.buildUserResponseDTO();
        testUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create user successfully and return 201 status")
    void testCreateUser_ValidData_ShouldReturnCreatedResponse() {
        // Given
        when(userService.createUser(any(UserCreateDTO.class)))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.createUser(testUserCreateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(testUserResponseDTO.getName());
        assertThat(response.getBody().getEmail()).isEqualTo(testUserResponseDTO.getEmail());

        // Verify service method was called
        verify(userService).createUser(testUserCreateDTO);
    }

    @Test
    @DisplayName("Should return all users successfully")
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        List<UserResponseDTO> users = TestDataBuilder.buildUserResponseDTOList(3);
        when(userService.getAllUsers()).thenReturn(users);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody()).containsExactlyElementsOf(users);

        // Verify service method was called
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsers_NoUsersExist_ShouldReturnEmptyList() {
        // Given
        List<UserResponseDTO> emptyUsers = Arrays.asList();
        when(userService.getAllUsers()).thenReturn(emptyUsers);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        // Verify service method was called
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_ValidId_ShouldReturnUser() {
        // Given
        when(userService.getUserById(testUserId)).thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.getUserById(testUserId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testUserResponseDTO.getId());
        assertThat(response.getBody().getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify service method was called
        verify(userService).getUserById(testUserId);
    }

    @Test
    @DisplayName("Should update user completely and return updated user")
    void testUpdateUser_ValidData_ShouldReturnUpdatedUser() {
        // Given
        when(userService.updateUser(any(UUID.class), any(UserCreateDTO.class)))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUser(testUserId, testUserCreateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify service method was called
        verify(userService).updateUser(testUserId, testUserCreateDTO);
    }

    @Test
    @DisplayName("Should update user partially and return updated user")
    void testUpdateUserPartially_ValidData_ShouldReturnUpdatedUser() {
        // Given
        when(userService.updateUserPartially(any(UUID.class), any(UserUpdateDTO.class)))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUserPartially(testUserId, testUserUpdateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify service method was called
        verify(userService).updateUserPartially(testUserId, testUserUpdateDTO);
    }

    @Test
    @DisplayName("Should delete user successfully and return 204 status")
    void testDeleteUser_ValidId_ShouldReturnNoContent() {
        // Given
        // No return value for delete operation

        // When
        ResponseEntity<Void> response = userController.deleteUser(testUserId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        // Verify service method was called
        verify(userService).deleteUser(testUserId);
    }

    @Test
    @DisplayName("Should create bulk users successfully and return 201 status")
    void testCreateBulkUsers_ValidData_ShouldReturnCreatedResponse() {
        // Given
        List<UserCreateDTO> userCreateDTOs = Arrays.asList(
                TestDataBuilder.buildUserCreateDTO("User1", "user1@example.com", 25),
                TestDataBuilder.buildUserCreateDTO("User2", "user2@example.com", 30),
                TestDataBuilder.buildUserCreateDTO("User3", "user3@example.com", 35)
        );

        List<UserResponseDTO> createdUsers = TestDataBuilder.buildUserResponseDTOList(3);
        when(userService.createBulkUsers(anyList())).thenReturn(createdUsers);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.createBulkUsers(userCreateDTOs);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody()).containsExactlyElementsOf(createdUsers);

        // Verify service method was called
        verify(userService).createBulkUsers(userCreateDTOs);
    }

    @Test
    @DisplayName("Should create bulk users with empty list and return empty response")
    void testCreateBulkUsers_EmptyList_ShouldReturnEmptyResponse() {
        // Given
        List<UserCreateDTO> emptyUserCreateDTOs = List.of();
        List<UserResponseDTO> emptyCreatedUsers = List.of();
        when(userService.createBulkUsers(anyList())).thenReturn(emptyCreatedUsers);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.createBulkUsers(emptyUserCreateDTOs);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        // Verify service method was called
        verify(userService).createBulkUsers(emptyUserCreateDTOs);
    }

    @Test
    @DisplayName("Should handle user creation with valid data")
    void testCreateUser_ValidData_ShouldPassToService() {
        // Given
        when(userService.createUser(testUserCreateDTO)).thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.createUser(testUserCreateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called
        verify(userService).createUser(testUserCreateDTO);
    }

    @Test
    @DisplayName("Should handle user update with valid data")
    void testUpdateUser_ValidData_ShouldPassToService() {
        // Given
        when(userService.updateUser(testUserId, testUserCreateDTO))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUser(testUserId, testUserCreateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called
        verify(userService).updateUser(testUserId, testUserCreateDTO);
    }

    @Test
    @DisplayName("Should handle partial user update with valid data")
    void testUpdateUserPartially_ValidData_ShouldPassToService() {
        // Given
        when(userService.updateUserPartially(testUserId, testUserUpdateDTO))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUserPartially(testUserId, testUserUpdateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called
        verify(userService).updateUserPartially(testUserId, testUserUpdateDTO);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123e4567-e89b-12d3-a456-426614174000",
        "987fcdeb-51a2-43d7-8f9e-123456789abc",
        "456789ab-cdef-1234-5678-9abcdef01234",
        "00000000-0000-0000-0000-000000000000"
    })
    @DisplayName("Should handle different UUID formats for getUserById")
    void testGetUserById_DifferentUuidFormats_ShouldPassToService(String uuidString) {
        // Given
        UUID differentUuid = UUID.fromString(uuidString);
        when(userService.getUserById(differentUuid)).thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.getUserById(differentUuid);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with specific UUID
        verify(userService).getUserById(differentUuid);
    }

    @Test
    @DisplayName("Should handle update with different UUID formats")
    void testUpdateUser_DifferentUuidFormats_ShouldPassToService() {
        // Given
        UUID differentUuid = UUID.fromString("987fcdeb-51a2-43d7-8f9e-123456789abc");
        when(userService.updateUser(any(UUID.class), any(UserCreateDTO.class)))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUser(differentUuid, testUserCreateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with specific UUID
        verify(userService).updateUser(differentUuid, testUserCreateDTO);
    }

    @Test
    @DisplayName("Should handle partial update with different UUID formats")
    void testUpdateUserPartially_DifferentUuidFormats_ShouldPassToService() {
        // Given
        UUID differentUuid = UUID.fromString("456789ab-cdef-1234-5678-9abcdef01234");
        when(userService.updateUserPartially(any(UUID.class), any(UserUpdateDTO.class)))
                .thenReturn(testUserResponseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUserPartially(differentUuid, testUserUpdateDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify service method was called with specific UUID
        verify(userService).updateUserPartially(differentUuid, testUserUpdateDTO);
    }

    @Test
    @DisplayName("Should handle delete with different UUID formats")
    void testDeleteUser_DifferentUuidFormats_ShouldPassToService() {
        // Given
        UUID differentUuid = UUID.fromString("fedcba98-7654-3210-fedc-ba9876543210");

        // When
        ResponseEntity<Void> response = userController.deleteUser(differentUuid);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        // Verify service method was called with specific UUID
        verify(userService).deleteUser(differentUuid);
    }
}
