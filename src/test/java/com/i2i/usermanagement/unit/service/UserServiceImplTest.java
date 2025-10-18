package com.i2i.usermanagement.unit.service;

import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserUpdateDTO;
import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.exception.UserAlreadyExistsException;
import com.i2i.usermanagement.exception.UserNotFoundException;
import com.i2i.usermanagement.mapper.UserMapper;
import com.i2i.usermanagement.repository.RoleRepository;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.repository.UserRoleRepository;
import com.i2i.usermanagement.service.impl.UserServiceImpl;
import com.i2i.usermanagement.task.BulkUserCreationTask;
import com.i2i.usermanagement.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserServiceImpl class.
 * Tests all business logic methods with proper mocking.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 16-10-2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDTO testUserCreateDTO;
    private UserUpdateDTO testUserUpdateDTO;
    private UserResponseDTO testUserResponseDTO;
    private User testUser;
    private Role testRole;
    private UserRole testUserRole;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        // Clear SecurityContext before each test
        SecurityContextHolder.clearContext();

        testUserCreateDTO = TestDataBuilder.buildUserCreateDTO();
        testUserUpdateDTO = TestDataBuilder.buildUserUpdateDTO();
        testUserResponseDTO = TestDataBuilder.buildUserResponseDTO();
        testUser = TestDataBuilder.buildUser();
        testRole = TestDataBuilder.buildRole("ROLE_USER");
        testUserRole = TestDataBuilder.buildUserRole(testUser, testRole);
        testUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create user successfully with valid data")
    void testCreateUser_ValidData_ShouldCreateUser() {
        // Given
        when(userRepository.existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail()))
                .thenReturn(false);
        when(userMapper.toEntity(testUserCreateDTO)).thenReturn(testUser);
        when(passwordEncoder.encode(testUserCreateDTO.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(testRole));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(testUserRole);

        // When
        UserResponseDTO result = userService.createUser(testUserCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getName()).isEqualTo(testUser.getName());


        // Verify interactions
        verify(userRepository).existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail());
        verify(userMapper).toEntity(testUserCreateDTO);
        verify(passwordEncoder).encode(testUserCreateDTO.getPassword());
        verify(userRepository).save(testUser);
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    @DisplayName("Should throw exception when user with email already exists")
    void testCreateUser_EmailAlreadyExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(testUserCreateDTO))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email " + testUserCreateDTO.getEmail() + " already exists");

        // Verify interactions
        verify(userRepository).existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail());
    }

    @Test
    @DisplayName("Should create user when ROLE_USER not found in database")
    void testCreateUser_RoleNotFound_ShouldCreateUserWithoutRole() {
        // Given
        when(userRepository.existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail()))
                .thenReturn(false);
        when(userMapper.toEntity(testUserCreateDTO)).thenReturn(testUser);
        when(passwordEncoder.encode(testUserCreateDTO.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // When
        UserResponseDTO result = userService.createUser(testUserCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getName()).isEqualTo(testUser.getName());

        // Verify interactions
        verify(userRepository).existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail());
        verify(userMapper).toEntity(testUserCreateDTO);
        verify(passwordEncoder).encode(testUserCreateDTO.getPassword());
        verify(userRepository).save(testUser);
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = TestDataBuilder.buildUserList(3);
        List<UserResponseDTO> userResponseDTOs = TestDataBuilder.buildUserResponseDTOList(3);

        when(userRepository.findByIsActiveTrue()).thenReturn(users);
        when(userMapper.toDTOList(users)).thenReturn(userResponseDTOs);

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(userResponseDTOs);

        // Verify interactions
        verify(userRepository).findByIsActiveTrue();
        verify(userMapper).toDTOList(users);
    }

    @Test
    @DisplayName("Should get user by ID as admin")
    void testGetUserById_AsAdmin_ShouldReturnUser() {
        // Given
        mockAdminAuthentication();
        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.getUserById(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
        verify(userMapper).toDTO(testUser);
    }

    @Test
    @DisplayName("Should get user by ID as regular user - return own data")
    void testGetUserById_AsUser_ShouldReturnOwnData() {
        // Given
        mockUserAuthentication();
        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.getUserById(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(userMapper).toDTO(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found as admin")
    void testGetUserById_AsAdmin_UserNotFound_ShouldThrowException() {
        // Given
        mockAdminAuthentication();
        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(testUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with ID: " + testUserId);

        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
    }

    @Test
    @DisplayName("Should throw exception when user not found as regular user")
    void testGetUserById_AsUser_UserNotFound_ShouldThrowException() {
        // Given
        mockUserAuthentication();
        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(testUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: testuser");

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
    }

    @Test
    @DisplayName("Should update user completely as admin")
    void testUpdateUser_AsAdmin_ShouldUpdateUser() {
        // Given - Create test user with different email to trigger email check
        User existingUser = TestDataBuilder.buildUser();
        existingUser.setEmail("old@example.com");  // Different email to trigger check

        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(testUserCreateDTO.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDTO(existingUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.updateUser(testUserId, testUserCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
        verify(userRepository).existsByEmailAndIsActiveTrue(testUserCreateDTO.getEmail());
        verify(passwordEncoder).encode(testUserCreateDTO.getPassword());
        verify(userRepository).save(existingUser);
        verify(userMapper).toDTO(existingUser);
    }


    @Test
    @DisplayName("Should update user partially as admin - all fields")
    void testUpdateUserPartially_AsAdmin_AllFields_ShouldUpdateUser() {
        // Given
        mockAdminAuthentication();
        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIsActiveTrue(testUserUpdateDTO.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(testUserUpdateDTO.getPassword()))
                .thenReturn("hashedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.updateUserPartially(testUserId, testUserUpdateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
        verify(userRepository).existsByEmailAndIsActiveTrue(testUserUpdateDTO.getEmail());
        verify(passwordEncoder).encode(testUserUpdateDTO.getPassword());
        verify(userRepository).save(testUser);
        verify(userMapper).toDTO(testUser);
    }

    @Test
    @DisplayName("Should update user partially as regular user - only phone and address")
    void testUpdateUserPartially_AsUser_PhoneAndAddress_ShouldUpdateUser() {
        // Given
        mockUserAuthentication();
        UserUpdateDTO limitedUpdateDTO = TestDataBuilder.buildPartialUserUpdateDTO(null, null);
        limitedUpdateDTO.setPhoneNumber("+9876543210");
        limitedUpdateDTO.setAddress("New Address");

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.updateUserPartially(testUserId, limitedUpdateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(userRepository).save(testUser);
        verify(userMapper).toDTO(testUser);
    }

    @Test
    @DisplayName("Should throw exception when regular user tries to update restricted fields")
    void testUpdateUserPartially_AsUser_RestrictedFields_ShouldIgnoreRestrictedFields() {
        // Given
        mockUserAuthentication();
        UserUpdateDTO restrictedUpdateDTO = TestDataBuilder.buildUserUpdateDTO();
        restrictedUpdateDTO.setName("New Name"); // Should be ignored
        restrictedUpdateDTO.setEmail("new@example.com"); // Should be ignored
        restrictedUpdateDTO.setAge(30); // Should be ignored
        restrictedUpdateDTO.setPhoneNumber("+9876543210"); // Should be allowed
        restrictedUpdateDTO.setAddress("New Address"); // Should be allowed

        when(userRepository.findByNameAndIsActiveTrue("testuser"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserResponseDTO);

        // When
        UserResponseDTO result = userService.updateUserPartially(testUserId, restrictedUpdateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testUserResponseDTO.getName());

        // Verify interactions
        verify(userRepository).findByNameAndIsActiveTrue("testuser");
        verify(userRepository).save(testUser);
        verify(userMapper).toDTO(testUser);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_ValidId_ShouldDeleteUser() {
        // Given
        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.of(testUser));

        // When
        userService.deleteUser(testUserId);

        // Then
        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
        verify(userRepository).softDeleteById(testUserId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUser_UserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findByIdAndIsActiveTrue(testUserId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(testUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with ID: " + testUserId);

        // Verify interactions
        verify(userRepository).findByIdAndIsActiveTrue(testUserId);
    }

    @Test
    @DisplayName("Should handle empty bulk user creation")
    void testCreateBulkUsers_EmptyList_ShouldReturnEmptyList() {
        // Given
        List<UserCreateDTO> emptyUserCreateDTOs = Arrays.asList();

        // When
        List<UserResponseDTO> result = userService.createBulkUsers(emptyUserCreateDTOs);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle null bulk user creation")
    void testCreateBulkUsers_NullList_ShouldHandleGracefully() {
        // Given
        List<UserCreateDTO> nullUserCreateDTOs = null;

        // When & Then
        assertThatThrownBy(() -> userService.createBulkUsers(nullUserCreateDTOs))
                .isInstanceOf(Exception.class); // ForkJoinPool will throw exception for null
    }

    // Helper methods for mocking authentication
    private void mockAdminAuthentication() {
        Authentication auth = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return "admin";
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "admin";
            }
        };
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void mockUserAuthentication() {
        Authentication auth = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return "testuser";
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "testuser";
            }
        };
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
