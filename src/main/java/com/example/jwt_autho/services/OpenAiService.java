package com.example.jwt_autho.services;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final ChatClient chatClient;

    @Autowired
    public OpenAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public boolean isProductDescriptionLegal(String description) {
        String prompt = "Check if the following product description involves selling any illegal products. " +
                    "Illegal products include animal products (such as selling dogs or other pets), weapons, drugs, or any other prohibited items. " +
                    "If the description mentions any of these, mark it as illegal. Product description: \"" + description + "\"" + " just return illegal or legal.";
    
        // Call OpenAI API with the crafted prompt
        String response = chatClient.prompt()
                                .user(prompt)
                                .call()
                                .content();
        
        return !response.toLowerCase().contains("illegal");
    }
}

