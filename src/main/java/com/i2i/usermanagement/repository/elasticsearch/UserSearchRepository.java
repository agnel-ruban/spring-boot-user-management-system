package com.i2i.usermanagement.repository.elasticsearch;

import com.i2i.usermanagement.document.UserDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Elasticsearch repository interface for User search operations.
 * Provides methods for searching users by name (fuzzy) and email (exact).
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, UUID> {

    /**
     * Searches users by name using fuzzy matching.
     * Supports typo tolerance for better search experience.
     *
     * @param name the name to search for
     * @return List of matching UserDocuments
     */
    @Query("{\"bool\": {\"should\": [{\"fuzzy\": {\"name\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}, {\"wildcard\": {\"name\": {\"value\": \"*?0*\", \"case_insensitive\": true}}}, {\"match\": {\"name\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}]}}")
    List<UserDocument> findByNameContaining(String name);

    /**
     * Searches users by email with partial matching.
     *
     * @param email the email to search for
     * @return List of matching UserDocuments
     */
    @Query("{\"wildcard\": {\"email\": {\"value\": \"*?0*\", \"case_insensitive\": true}}}")
    List<UserDocument> findByEmail(String email);
}
