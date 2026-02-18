package com.ttn.springai.config;

import com.ttn.springai.advisors.LogAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfiguration {

    private final ChatClient.Builder builder;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final LogAdvisor logAdvisor;


    public ChatClientConfiguration(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository, LogAdvisor logAdvisor) {
        this.builder = builder;
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
        this.logAdvisor = logAdvisor;
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    @Bean
    public ChatClient chatClient() {
        return builder.defaultAdvisors(logAdvisor).build();
    }

    @Bean("chatClientWithMemory")
    public ChatClient chatClientWithMemory() {
        return builder.defaultAdvisors(chatMemoryAdvisor()).build();
    }

    @Bean("chatMemoryAdvisor")
    MessageChatMemoryAdvisor chatMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(chatMemory()).build();
    }

    @Bean("chatClientForTools")
    public ChatClient chatClientForTools(SyncMcpToolCallbackProvider toolCallbackProvider) {
        return builder.defaultAdvisors(chatMemoryAdvisor()).defaultToolCallbacks(toolCallbackProvider.getToolCallbacks()).build();
    }

}
