package com.i2i.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Role entity representing user roles in the system.
 * Supports role-based access control (RBAC).
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Name of the role (e.g., ROLE_ADMIN, ROLE_USER).
     * Must be unique and not blank.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
