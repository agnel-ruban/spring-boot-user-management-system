package com.i2i.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Generic base repository interface for entities with soft delete functionality.
 * Provides common CRUD operations with soft delete support.
 *
 * @param <T> the entity type
 * @param <ID> the ID type
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Finds all active entities (isActive = true).
     *
     * @return list of active entities
     */
    List<T> findByIsActiveTrue();

    /**
     * Finds an active entity by ID (isActive = true).
     *
     * @param id the entity ID
     * @return Optional containing the active entity if found
     */
    Optional<T> findByIdAndIsActiveTrue(ID id);

    /**
     * Soft deletes an entity by setting isActive to false.
     *
     * @param id the entity ID to softly delete
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} e SET e.isActive = false WHERE e.id = :id")
    void softDeleteById(@Param("id") ID id);
}
