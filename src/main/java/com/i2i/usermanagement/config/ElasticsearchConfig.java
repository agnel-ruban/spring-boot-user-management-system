package com.i2i.usermanagement.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuration class for Elasticsearch client setup.
 * Configures the Elasticsearch Java API client and enables repository scanning.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 23-10-2025
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.i2i.usermanagement.repository.elasticsearch")
public class ElasticsearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Value("${elasticsearch.host:localhost}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port:9200}")
    private int elasticsearchPort;

    @Value("${elasticsearch.scheme:http}")
    private String elasticsearchScheme;

    /**
     * Creates and configures the Elasticsearch client bean.
     * Uses the Java API client for better performance and type safety.
     *
     * @return configured ElasticsearchClient instance
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        try {
            logger.info("Configuring Elasticsearch client for {}://{}:{}",
                       elasticsearchScheme, elasticsearchHost, elasticsearchPort);

            // Create the low-level client
            RestClient restClient = RestClient.builder(
                new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchScheme)
            ).build();

            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
            );

            // Create the API client
            ElasticsearchClient client = new ElasticsearchClient(transport);

            logger.info("Elasticsearch client configured successfully");
            return client;

        } catch (Exception e) {
            logger.error("Failed to configure Elasticsearch client", e);
            throw new RuntimeException("Failed to initialize Elasticsearch client", e);
        }
    }
}
