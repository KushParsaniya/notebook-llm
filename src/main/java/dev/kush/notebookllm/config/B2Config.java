package dev.kush.notebookllm.config;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class B2Config {

    @Value("${b2.application.key.id}")
    private String applicationKeyId;

    @Value("${b2.application.key.value}")
    private String applicationKey;

    @Value("${b2.application.key.name}")
    private String applicationKeyName;

    @Bean
    B2StorageClient b2StorageClient() {
        return B2StorageClientFactory.createDefaultFactory()
                .create(applicationKeyId, applicationKey, "notebook-llm");
    }

    @Bean
    ExecutorService b2ExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
