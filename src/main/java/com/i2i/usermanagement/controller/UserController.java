package com.i2i.usermanagement.controller;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;
import com.i2i.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User management operations.
 * Provides basic CRUD operations for users.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Constructor for dependency injection.
     * 
     * @param userService the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     * Only ADMIN can create users.
     * 
     * @param userCreateDTO the user data for creation
     * @return ResponseEntity containing the created user (only id and name)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        logger.info("Creating new user with email: {}", userCreateDTO.getEmail());
        UserResponseDTO createdUser = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Retrieves all users.
     * Only ADMIN can access this endpoint.
     * 
     * @return ResponseEntity containing the list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by ID.
     * Both ADMIN and USER can access, but USER can only see their own data.
     * 
     * @param id the unique identifier of the user
     * @return ResponseEntity containing the user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates an existing user completely.
     * Only ADMIN can update users completely.
     * 
     * @param id the unique identifier of the user to update
     * @param userCreateDTO the updated user data
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id,
                                            @Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userCreateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Updates an existing user partially.
     * Both ADMIN and USER can access, but USER can only update their own data (phone, address).
     * 
     * @param id the unique identifier of the user to update
     * @param userUpdateDTO the updated user data
     * @return ResponseEntity containing the updated user
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<UserResponseDTO> updateUserPartially(@PathVariable UUID id,
                                                      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUserPartially(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by ID.
     * Only ADMIN can delete users.
     *
     * @param id the unique identifier of the user to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates multiple users in parallel using Fork/Join.
     * Only ADMIN can create bulk users.
     *
     * @param userCreateDTOs list of user data for creation
     * @return ResponseEntity containing the list of created users
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> createBulkUsers(@Valid @RequestBody List<UserCreateDTO> userCreateDTOs) {
        logger.info("Creating {} users in bulk using Fork/Join", userCreateDTOs.size());
        List<UserResponseDTO> createdUsers = userService.createBulkUsers(userCreateDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
    }
}
