package com.example.jwt_autho.configs;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    // @Value("${spring.ai.openai.api-key}")
    // private String apiKey;

    @Bean
    public ChatClient chatClient(Builder builder) {
        return builder
            // .apiKey(apiKey) // The API key is set here
            .build();
    }
}
