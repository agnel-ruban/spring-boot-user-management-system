package com.i2i.usermanagement.config;

import com.i2i.usermanagement.entity.Role;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.entity.UserRole;
import com.i2i.usermanagement.repository.RoleRepository;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.repository.UserRoleRepository;
import com.i2i.usermanagement.service.UserProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner to initialize admin user at application startup.
 * Creates admin user with hashed password if it doesn't exist.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;

    public AdminUserInitializer(UserRepository userRepository, RoleRepository roleRepository,
                               UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder,
                               UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileService = userProfileService;
    }

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userRepository.findByNameAndIsActiveTrue(adminUsername).isEmpty()) {
            // Create admin user
            String hashedPassword = passwordEncoder.encode(adminPassword);

            User adminUser = User.builder()
                    .name(adminUsername)
                    .email("admin@example.com")
                    .password(hashedPassword)
                    .isActive(true)
                    .build();

            // Save admin user to PostgreSQL
            User savedAdminUser = userRepository.save(adminUser);

            // Create admin profile in MongoDB
            userProfileService.createOrUpdateProfile(
                savedAdminUser.getId(),
                30,
                "7845928778",
                "john street"
            );

            // Assign ROLE_ADMIN to admin user
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
            if (adminRole != null) {
                UserRole userRole = UserRole.builder()
                        .user(savedAdminUser)
                        .role(adminRole)
                        .build();
                userRoleRepository.save(userRole);
            }
        }
    }
}
