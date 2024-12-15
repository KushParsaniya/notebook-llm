package dev.kush.notebookllm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "datastax.astra")
@Getter
@Setter
public class AstraProperties {

    private File secureConnectBundle;
}
