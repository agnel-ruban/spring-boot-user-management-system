package com.i2i.usermanagement.config;

import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.repository.UserRepository;
import com.i2i.usermanagement.service.UserDataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component to initialize Elasticsearch with existing user data on application startup.
 * This ensures that existing users in PostgresSQL are indexed in Elasticsearch.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Component
@Order(2)
public class ElasticsearchInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchInitializer.class);
    
    private final UserRepository userRepository;
    private final UserDataSyncService userDataSyncService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository the user repository
     * @param userDataSyncService the user data sync service
     */
    public ElasticsearchInitializer(UserRepository userRepository, UserDataSyncService userDataSyncService) {
        this.userRepository = userRepository;
        this.userDataSyncService = userDataSyncService;
    }

    /**
     * Runs on application startup to sync existing users to Elasticsearch.
     *
     * @param args command line arguments
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Starting Elasticsearch initialization...");

            // Get all active users from PostgresSQL
            List<User> activeUsers = userRepository.findByIsActiveTrue();

            if (activeUsers.isEmpty()) {
                logger.info("No active users found in PostgresSQL. Skipping Elasticsearch initialization.");
                return;
            }

            logger.info("Found {} active users in PostgresSQL. Starting Elasticsearch sync...", activeUsers.size());

            // Bulk index all active users
            int indexedCount = userDataSyncService.reindexAllUsers(activeUsers);

            logger.info("Elasticsearch initialization completed successfully. Indexed {} users.", indexedCount);

        } catch (Exception e) {
            logger.error("Failed to initialize Elasticsearch with existing user data", e);
            logger.warn("Application will continue without Elasticsearch initialization");
        }
    }
}
