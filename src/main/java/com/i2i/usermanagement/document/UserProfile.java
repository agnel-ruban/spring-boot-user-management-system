package com.i2i.usermanagement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * MongoDB document for user profile information.
 * Stores extended user data (age, phoneNumber, address) separate from PostgreSQL.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 27-01-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String id;

    /**
     * Reference to PostgresSQL user ID
     */
    private UUID userId;

    /**
     * User's age
     */
    private Integer age;

    /**
     * User's phone number
     */
    private String phoneNumber;

    /**
     * User's address
     */
    private String address;
}
