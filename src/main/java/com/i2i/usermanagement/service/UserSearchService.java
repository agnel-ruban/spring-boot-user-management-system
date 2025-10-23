package com.i2i.usermanagement.service;

import com.i2i.usermanagement.document.UserDocument;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.repository.elasticsearch.UserSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User search operations using Elasticsearch.
 * Provides fuzzy search for name and exact search for email.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Service
public class UserSearchService {

    private static final Logger logger = LoggerFactory.getLogger(UserSearchService.class);

    private final UserSearchRepository userSearchRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param userSearchRepository the user search repository
     */
    public UserSearchService(UserSearchRepository userSearchRepository) {
        this.userSearchRepository = userSearchRepository;
    }


    /**
     * Searches users by name with fuzzy matching (typo tolerance).
     *
     * @param name the name to search for
     * @return List of UserResponseDTOs
     */
    public List<UserResponseDTO> searchUsersByName(String name) {
        try {
            logger.info("Searching users by name: '{}'", name);

            List<UserDocument> userDocuments = userSearchRepository.findByNameContaining(name);

            return userDocuments.stream()
                    .map(this::convertToUserResponseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error searching users by name: '{}'", name, e);
            throw new RuntimeException("Name search operation failed", e);
        }
    }

    /**
     * Searches users by email (exact match).
     *
     * @param email the email to search for
     * @return List of UserResponseDTOs
     */
    public List<UserResponseDTO> searchUsersByEmail(String email) {
        try {
            logger.info("Searching users by email: '{}'", email);

            List<UserDocument> userDocuments = userSearchRepository.findByEmail(email);

            return userDocuments.stream()
                    .map(this::convertToUserResponseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error searching users by email: '{}'", email, e);
            throw new RuntimeException("Email search operation failed", e);
        }
    }


    /**
     * Converts UserDocument to UserResponseDTO.
     *
     * @param userDocument the UserDocument to convert
     * @return converted UserResponseDTO
     */
    private UserResponseDTO convertToUserResponseDTO(UserDocument userDocument) {
        try {
            return UserResponseDTO.builder()
                    .id(userDocument.getId())
                    .name(userDocument.getName())
                    .email(userDocument.getEmail())
                    .age(userDocument.getAge())
                    .phoneNumber(userDocument.getPhoneNumber())
                    .address(userDocument.getAddress())
                    .isActive(userDocument.getIsActive())
                    .createdAt(userDocument.getCreatedAt())
                    .updatedAt(userDocument.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            logger.error("Error converting UserDocument to UserResponseDTO for user: {}", userDocument.getId(), e);
            throw new RuntimeException("Document conversion failed", e);
        }
    }
}
