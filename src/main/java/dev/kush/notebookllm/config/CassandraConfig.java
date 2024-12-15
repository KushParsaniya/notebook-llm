package dev.kush.notebookllm.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Bean
    CqlSessionBuilderCustomizer cqlSessionBuilderCustomizer(AstraProperties astraProperties) {
        return builder -> builder.withCloudSecureConnectBundle(astraProperties.getSecureConnectBundle().toPath());
    }
}
