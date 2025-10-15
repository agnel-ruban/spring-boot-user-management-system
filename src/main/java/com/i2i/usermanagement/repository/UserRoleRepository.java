package com.i2i.usermanagement.repository;

import com.i2i.usermanagement.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for UserRole entity operations.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

}
