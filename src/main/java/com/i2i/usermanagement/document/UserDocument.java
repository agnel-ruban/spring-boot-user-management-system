package com.i2i.usermanagement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Elasticsearch document representing a user for search operations.
 * This document is optimized for search and indexing operations.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Document(indexName = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    /**
     * Unique identifier for the user document.
     * Maps to the PostgreSQL user ID for consistency.
     */
    @Id
    private UUID id;

    /**
     * Full name of the user.
     * Indexed as text for full-text search with keyword subfield for exact matches.
     */
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String name;

    /**
     * Email address of the user.
     * Indexed as keyword for exact matching and filtering.
     */
    @Field(type = FieldType.Keyword)
    private String email;

    /**
     * Age of the user.
     * Indexed as integer for range queries and filtering.
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * Phone number of the user.
     * Indexed as keyword for exact matching.
     */
    @Field(type = FieldType.Keyword)
    private String phoneNumber;

    /**
     * Address of the user.
     * Indexed as text for full-text search.
     */
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String address;

    /**
     * Indicates whether the user account is active.
     * Indexed as boolean for filtering.
     */
    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    /**
     * Timestamp when the user record was created.
     * Indexed as date for range queries and sorting.
     */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user record was last updated.
     * Indexed as date for range queries and sorting.
     */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

}
