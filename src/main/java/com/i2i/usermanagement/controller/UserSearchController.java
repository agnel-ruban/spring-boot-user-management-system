package com.i2i.usermanagement.controller;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.service.UserSearchService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for User search operations using Elasticsearch.
 * Provides advanced search capabilities with fuzzy matching and filtering.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@RestController
@RequestMapping("/api/v1/users/search")
@Validated
public class UserSearchController {

    private static final Logger logger = LoggerFactory.getLogger(UserSearchController.class);
    
    private final UserSearchService userSearchService;

    /**
     * Constructor for dependency injection.
     *
     * @param userSearchService the user search service
     */
    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    /**
     * Searches users by name with fuzzy matching.
     *
     * @param name the name to search for
     * @return ResponseEntity containing list of matching users
     */
    @GetMapping("/name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByName(
            @RequestParam @NotBlank(message = "Name is required") String name) {

        logger.info("Name search request - name: '{}'", name);

        List<UserResponseDTO> results = userSearchService.searchUsersByName(name);
        return ResponseEntity.ok(results);
    }

    /**
     * Searches users by email.
     *
     * @param email the email to search for
     * @return ResponseEntity containing list of matching users
     */
    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByEmail(
            @RequestParam @NotBlank(message = "Email is required") String email) {

        logger.info("Email search request - email: '{}'", email);

        List<UserResponseDTO> results = userSearchService.searchUsersByEmail(email);
        return ResponseEntity.ok(results);
    }

}
