package com.ttn.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import java.util.Map;

@RestController
@Slf4j
public class ChatController {

    private final ChatClient chatClient;
    @Qualifier("chatClientWithMemory")
    private final ChatClient chatClientWithMemory;
    @Value("classpath:prompts/system-prompt.st")
    Resource systemTemplate;
    private final ChatClient chatClientForTools;

    @Value("classpath:prompts/system-prompt-mcp.st")
    Resource systemTemplateMcp;

    public ChatController(ChatClient chatClient, @Qualifier("chatClientWithMemory") ChatClient chatClientWithMemory, @Qualifier("chatClientForTools") ChatClient chatClientForTools) {
        this.chatClient = chatClient;
        this.chatClientWithMemory = chatClientWithMemory;
        this.chatClientForTools = chatClientForTools;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt(message).call().content();
    }


    @GetMapping("/chatWithMemory")
    public String chat(@RequestParam String message, @RequestHeader(name = "username") String username) {
        return chatClientWithMemory.prompt()
        .advisors(spec -> spec.param(CONVERSATION_ID, username))
        .user(message).call().content();
    }


    @GetMapping("/chatWithHRBot")
    public String chatWithHRBot(@RequestParam String message, @RequestHeader(name = "username") String username) {
        return chatClientWithMemory.prompt()
        .advisors(spec -> spec.param(CONVERSATION_ID, username))
        .system(systemTemplate)
        .user(message).call().content();
    }

    @GetMapping("/chat_with_helpdesk_bot")
    public String chatWithHelpdeskBot(@RequestParam String query, @RequestHeader(name = "username") String username) {
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemTemplateMcp);
        return chatClientForTools.prompt(systemTemplate.create(Map.of("username", username)))
                                .advisors(spec -> spec.param(CONVERSATION_ID, username))
                                .user(query).call().content();
    }

}
