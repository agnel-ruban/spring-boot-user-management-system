package com.i2i.usermanagement.service;

import com.i2i.usermanagement.document.UserDocument;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.repository.elasticsearch.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for synchronizing data between PostgresSQL and Elasticsearch.
 * Handles indexing, updating, and deleting user documents in Elasticsearch.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Service
public class UserDataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataSyncService.class);

    private final UserSearchRepository userSearchRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param userSearchRepository the user search repository
     */
    public UserDataSyncService(UserSearchRepository userSearchRepository) {
        this.userSearchRepository = userSearchRepository;
    }

    /**
     * Indexes a user document in Elasticsearch.
     *
     * @param user the User entity to index
     */
    public void indexUser(User user) {
        try {
            logger.debug("Indexing user with ID: {}", user.getId());

            UserDocument userDocument = convertToUserDocument(user);
            UserDocument indexedDocument = userSearchRepository.save(userDocument);

            logger.info("Successfully indexed user with ID: {}", user.getId());
        } catch (Exception e) {
            logger.error("Failed to index user with ID: {}", user.getId(), e);
            throw new RuntimeException("Failed to index user in Elasticsearch", e);
        }
    }

    /**
     * Updates an existing user document in Elasticsearch.
     *
     * @param user the updated User entity
     */
    public void updateUser(User user) {
        try {
            logger.debug("Updating user document with ID: {}", user.getId());

            UserDocument userDocument = convertToUserDocument(user);
            UserDocument updatedDocument = userSearchRepository.save(userDocument);

            logger.info("Successfully updated user document with ID: {}", user.getId());
        } catch (Exception e) {
            logger.error("Failed to update user document with ID: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user document in Elasticsearch", e);
        }
    }

    /**
     * Deletes a user document from Elasticsearch.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(UUID userId) {
        try {
            logger.debug("Deleting user document with ID: {}", userId);
            userSearchRepository.deleteById(userId);
            logger.info("Successfully deleted user document with ID: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to delete user document with ID: {}", userId, e);
            throw new RuntimeException("Failed to delete user document from Elasticsearch", e);
        }
    }

    /**
     * Bulk indexes multiple user documents in Elasticsearch.
     *
     * @param users the list of User entities to index
     * @return the list of indexed UserDocuments
     */
    public List<UserDocument> bulkIndexUsers(List<User> users) {
        try {
            logger.info("Bulk indexing {} users", users.size());

            List<UserDocument> userDocuments = users.stream()
                .map(this::convertToUserDocument)
                .collect(Collectors.toList());

            Iterable<UserDocument> indexedDocuments = userSearchRepository.saveAll(userDocuments);
            List<UserDocument> result = (List<UserDocument>) indexedDocuments;

            logger.info("Successfully bulk indexed {} users", result.size());
            return result;

        } catch (Exception e) {
            logger.error("Failed to bulk index users", e);
            throw new RuntimeException("Failed to bulk index users in Elasticsearch", e);
        }
    }

    /**
     * Reindex all users from PostgresSQL to Elasticsearch.
     * This method should be called during application startup or maintenance.
     *
     * @param users the list of all User entities from PostgresSQL
     * @return the number of successfully indexed documents
     */
    public int reindexAllUsers(List<User> users) {
        try {
            logger.info("Starting full reindex of {} users", users.size());

            // Clear existing index
            userSearchRepository.deleteAll();
            logger.info("Cleared existing Elasticsearch index");

            // Bulk index all users
            List<UserDocument> indexedDocuments = bulkIndexUsers(users);

            logger.info("Successfully completed full reindex of {} users", indexedDocuments.size());
            return indexedDocuments.size();

        } catch (Exception e) {
            logger.error("Failed to reindex all users", e);
            throw new RuntimeException("Failed to reindex all users in Elasticsearch", e);
        }
    }

    /**
     * Converts a User entity to a UserDocument for Elasticsearch indexing.
     *
     * @param user the User entity to convert
     * @return the converted UserDocument
     */
    private UserDocument convertToUserDocument(User user) {
        try {
            return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        } catch (Exception e) {
            logger.error("Failed to convert User to UserDocument for user ID: {}", user.getId(), e);
            throw new RuntimeException("Failed to convert User to UserDocument", e);
        }
    }
}
