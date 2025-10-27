package com.i2i.usermanagement.repository;

import com.i2i.usermanagement.document.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * MongoDB repository for UserProfile documents.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 27-01-2025
 */
@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    /**
     * Find user profile by PostgreSQL user ID
     *
     * @param userId the PostgreSQL user ID
     * @return UserProfile document or null if not found
     */
    UserProfile findByUserId(UUID userId);

    /**
     * Delete user profile by PostgreSQL user ID
     *
     * @param userId the PostgreSQL user ID
     */
    void deleteByUserId(UUID userId);
}
