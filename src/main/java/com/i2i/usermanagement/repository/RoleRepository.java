package com.i2i.usermanagement.repository;

import com.i2i.usermanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Role entity operations.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Finds a role by name.
     *
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
}
