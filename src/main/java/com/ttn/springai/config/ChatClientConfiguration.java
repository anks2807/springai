package com.ttn.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    private final ChatClient.Builder builder;
    private final ChatMemory chatMemory;

    @Autowired
    public ChatClientConfiguration(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.builder = builder;
        this.chatMemory = chatMemory;
    }

    @Bean
    public ChatClient chatClient() {
        return builder.build();
    }

    @Bean("chatClientWithMemory")
    public ChatClient chatClientWithMemory() {
        return builder.defaultAdvisors(chatMemoryAdvisor()).build();
    }

    @Bean("chatMemoryAdvisor")
    MessageChatMemoryAdvisor chatMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

}
