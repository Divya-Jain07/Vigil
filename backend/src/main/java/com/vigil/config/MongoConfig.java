package com.vigil.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/vigil}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        log.info("Initializing MongoDB client connection...");
        return MongoClients.create(mongoUri);
    }
}
