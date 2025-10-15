package com.i2i.usermanagement.repository;

import com.i2i.usermanagement.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations.
 * Extends BaseRepository to provide CRUD operations with soft delete support.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    /**
     * Checks if an active user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if active user exists, false otherwise
     */
    boolean existsByEmailAndIsActiveTrue(String email);


    /**
     * Finds an active user by username (name field).
     *
     * @param name the username to search for
     * @return Optional containing the active user if found
     */
    Optional<User> findByNameAndIsActiveTrue(String name);
}
