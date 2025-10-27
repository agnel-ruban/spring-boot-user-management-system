package com.i2i.usermanagement.service;

import com.i2i.usermanagement.document.UserProfile;
import com.i2i.usermanagement.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing user profile data in MongoDB.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 27-01-2025
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Save or update user profile
     *
     * @param userProfile the user profile to save
     * @return saved user profile
     */
    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    /**
     * Get user profile by PostgreSQL user ID
     *
     * @param userId the PostgreSQL user ID
     * @return user profile or null if not found
     */
    public UserProfile getUserProfile(UUID userId) {
        return userProfileRepository.findByUserId(userId);
    }

    /**
     * Delete user profile by PostgreSQL user ID
     *
     * @param userId the PostgreSQL user ID
     */
    public void deleteUserProfile(UUID userId) {
        userProfileRepository.deleteByUserId(userId);
    }

    /**
     * Create or update user profile with basic fields
     *
     * @param userId the PostgreSQL user ID
     * @param age user's age
     * @param phoneNumber user's phone number
     * @param address user's address
     * @return saved user profile
     */
    public UserProfile createOrUpdateProfile(UUID userId, Integer age, String phoneNumber, String address) {
        UserProfile existingProfile = getUserProfile(userId);

        if (existingProfile != null) {
            // Update existing profile
            existingProfile.setAge(age);
            existingProfile.setPhoneNumber(phoneNumber);
            existingProfile.setAddress(address);
            return saveUserProfile(existingProfile);
        } else {
            // Create new profile
            UserProfile newProfile = UserProfile.builder()
                    .userId(userId)
                    .age(age)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .build();
            return saveUserProfile(newProfile);
        }
    }
}
