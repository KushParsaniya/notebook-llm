package dev.kush.notebookllm;

import dev.kush.notebookllm.config.AstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AstraProperties.class)
public class NotebookLlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotebookLlmApplication.class, args);
    }

}
