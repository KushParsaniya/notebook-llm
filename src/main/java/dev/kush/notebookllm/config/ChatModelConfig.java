package dev.kush.notebookllm.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelConfig {

    @Bean
    @Primary
    ChatModel openAiChatModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
        OpenAiApi openAiApi = new OpenAiApi("https://api.groq.com/openai",apiKey);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("llama-3.2-90b-vision-preview")
                .build();
        return new OpenAiChatModel(openAiApi, openAiChatOptions);
    }

    @Bean
    ChatModel ollamaChatModel(@Value("${spring.ai.ollama.base-url}") String baseUrl) {
        return OllamaChatModel.builder()
                .withOllamaApi(new OllamaApi("http://%s:11434".formatted(baseUrl)))
                .withDefaultOptions(OllamaOptions
                        .builder()
                        .withModel("moondream")
                        .build())
                .build();
    }
}
