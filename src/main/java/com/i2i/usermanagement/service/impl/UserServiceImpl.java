package com.i2i.usermanagement.service.impl;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;
import com.i2i.usermanagement.document.UserProfile;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.exception.UserAlreadyExistsException;
import com.i2i.usermanagement.exception.UserNotFoundException;
import com.i2i.usermanagement.mapper.UserMapper;
import com.i2i.usermanagement.repository.RoleRepository;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.repository.UserRoleRepository;
import com.i2i.usermanagement.service.UserService;
import com.i2i.usermanagement.service.UserProfileService;
import com.i2i.usermanagement.task.BulkUserCreationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Provides business logic for user management operations.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository the user repository
     * @param roleRepository the role repository
     * @param userRoleRepository the user role repository
     * @param userMapper     the user mapper
     * @param passwordEncoder the password encoder
     * @param userProfileService the user profile service
     */
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                          UserRoleRepository userRoleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                          UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userProfileService = userProfileService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        // Check if active user already exists with this email
        if (userRepository.existsByEmailAndIsActiveTrue(userCreateDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userCreateDTO.getEmail() + " already exists");
        }

        // Convert DTO to entity
        User user = userMapper.toEntity(userCreateDTO);

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Assign ROLE_USER to the new user
        Role role = roleRepository.findByName("ROLE_USER").orElse(null);
        if (role != null) {
            UserRole userRoleMapping = UserRole.builder()
                    .user(savedUser)
                    .role(role)
                    .build();
            userRoleRepository.save(userRoleMapping);
        }

        // Save extended profile data to MongoDB
        UserProfile profile = userProfileService.createOrUpdateProfile(
            savedUser.getId(),
            userCreateDTO.getAge(),
            userCreateDTO.getPhoneNumber(),
            userCreateDTO.getAddress()
        );

        // Get combined data for response
        return userMapper.toCombinedDTO(savedUser, profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(user -> {
                    UserProfile profile = userProfileService.getUserProfile(user.getId());
                    return userMapper.toCombinedDTO(user, profile);
                })
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin can see any user
            User user = userRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
            UserProfile profile = userProfileService.getUserProfile(user.getId());
            return userMapper.toCombinedDTO(user, profile);
        } else {
            // User can only see their own data
            String username = authentication.getName();
            User user = userRepository.findByNameAndIsActiveTrue(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
            UserProfile profile = userProfileService.getUserProfile(user.getId());
            return userMapper.toCombinedDTO(user, profile);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO updateUser(UUID id, UserCreateDTO userCreateDTO) {
        User existingUser = userRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Check if email is being updated and if it already exists for active users
        if (!userCreateDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmailAndIsActiveTrue(userCreateDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userCreateDTO.getEmail() + " already exists");
        }

        // Update core fields in PostgresSQL
        existingUser.setName(userCreateDTO.getName());
        existingUser.setEmail(userCreateDTO.getEmail());

        // Hash the password if provided
        if (userCreateDTO.getPassword() != null && !userCreateDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        }

        // Save updated user to PostgresSQL
        User updatedUser = userRepository.save(existingUser);

        // Update extended fields in MongoDB
        UserProfile profile = userProfileService.createOrUpdateProfile(
            updatedUser.getId(),
            userCreateDTO.getAge(),
            userCreateDTO.getPhoneNumber(),
            userCreateDTO.getAddress()
        );

        // Get combined data for response
        return userMapper.toCombinedDTO(updatedUser, profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO updateUserPartially(UUID id, UserUpdateDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        User existingUser;
        if (isAdmin) {
            // Admin can update any user
            existingUser = userRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        } else {
            // User can only update their own data
            String username = authentication.getName();
            existingUser = userRepository.findByNameAndIsActiveTrue(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        }

        // Check if email is being updated and if it already exists for active users
        if (userUpdateDTO.getEmail() != null &&
            !userUpdateDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmailAndIsActiveTrue(userUpdateDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userUpdateDTO.getEmail() + " already exists");
        }

        // Update core fields in PostgresSQL
        if (isAdmin) {
            // Admin can update all core fields
            if (userUpdateDTO.getName() != null) {
                existingUser.setName(userUpdateDTO.getName());
            }
            if (userUpdateDTO.getEmail() != null) {
                existingUser.setEmail(userUpdateDTO.getEmail());
            }
            if (userUpdateDTO.getIsActive() != null) {
                existingUser.setIsActive(userUpdateDTO.getIsActive());
            }
            if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
            }
        }

        // age, phoneNumber, address are now handled in MongoDB below

        User updatedUser = userRepository.save(existingUser);

        // Update extended fields in MongoDB if provided
        UserProfile profile;
        if (userUpdateDTO.getAge() != null || userUpdateDTO.getPhoneNumber() != null || userUpdateDTO.getAddress() != null) {
            // Get current profile or create new one
            UserProfile currentProfile = userProfileService.getUserProfile(updatedUser.getId());

            Integer age = (userUpdateDTO.getAge() != null) ? userUpdateDTO.getAge() :
                         (currentProfile != null) ? currentProfile.getAge() : null;
            String phoneNumber = (userUpdateDTO.getPhoneNumber() != null) ? userUpdateDTO.getPhoneNumber() :
                                (currentProfile != null) ? currentProfile.getPhoneNumber() : null;
            String address = (userUpdateDTO.getAddress() != null) ? userUpdateDTO.getAddress() :
                            (currentProfile != null) ? currentProfile.getAddress() : null;

            profile = userProfileService.createOrUpdateProfile(updatedUser.getId(), age, phoneNumber, address);
        } else {
            // No MongoDB fields to update, get existing profile
            profile = userProfileService.getUserProfile(updatedUser.getId());
        }

        // Get combined data for response
        return userMapper.toCombinedDTO(updatedUser, profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(UUID id) {
        // Check if active user exists
        if (userRepository.findByIdAndIsActiveTrue(id).isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        // Soft delete: set isActive to false
        userRepository.softDeleteById(id);

        // Delete user profile from MongoDB
        userProfileService.deleteUserProfile(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> createBulkUsers(List<UserCreateDTO> userCreateDTOs) {
        // Create Fork/Join task
        BulkUserCreationTask task = new BulkUserCreationTask(userCreateDTOs, 0, userCreateDTOs.size(),this);

        // Execute using common pool
        return ForkJoinPool.commonPool().invoke(task);
    }
}
