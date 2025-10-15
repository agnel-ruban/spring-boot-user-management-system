package com.i2i.usermanagement.service;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for User management operations.
 * Defines the contract for user-related business logic.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
public interface UserService {

    /**
     * Creates a new user.
     * 
     * @param userCreateDTO the user data for creation
     * @return UserResponseDTO representing the created user
     * @throws UserAlreadyExistsException if user with same email already exists
     */
    UserResponseDTO createUser(UserCreateDTO userCreateDTO);

    /**
     * Retrieves all users.
     * 
     * @return List of all UserResponseDTOs
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Retrieves a user by ID.
     * 
     * @param id the unique identifier of the user
     * @return UserResponseDTO representing the user
     * @throws UserNotFoundException if user is not found
     */
    UserResponseDTO getUserById(UUID id);

    /**
     * Updates an existing user completely.
     * 
     * @param id the unique identifier of the user to update
     * @param userCreateDTO the updated user data
     * @return UserResponseDTO representing the updated user
     * @throws UserNotFoundException if user is not found
     */
    UserResponseDTO updateUser(UUID id, UserCreateDTO userCreateDTO);

    /**
     * Updates an existing user partially.
     * 
     * @param id the unique identifier of the user to update
     * @param userUpdateDTO the updated user data
     * @return UserResponseDTO representing the updated user
     * @throws UserNotFoundException if user is not found
     */
    UserResponseDTO updateUserPartially(UUID id, UserUpdateDTO userUpdateDTO);

    /**
     * Soft deletes a user by ID (sets isActive to false).
     *
     * @param id the unique identifier of the user to delete
     * @throws UserNotFoundException if user is not found
     */
    void deleteUser(UUID id);

    /**
     * Creates multiple users in parallel using Fork/Join.
     *
     * @param userCreateDTOs list of user data for creation
     * @return List of UserResponseDTOs representing the created users
     */
    List<UserResponseDTO> createBulkUsers(List<UserCreateDTO> userCreateDTOs);
}
