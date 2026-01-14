package com.ttn.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    private final ChatClient.Builder builder;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;

    public ChatClientConfiguration(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        this.builder = builder;
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).chatMemoryRepository(jdbcChatMemoryRepository).build();
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
        return MessageChatMemoryAdvisor.builder(chatMemory()).build();
    }

}
