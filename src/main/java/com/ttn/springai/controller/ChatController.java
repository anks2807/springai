package com.ttn.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController {

    private final ChatClient chatClient;
    @Qualifier("chatClientWithMemory")
    private final ChatClient chatClientWithMemory;
    @Value("classpath:prompts/system-prompt.st")
    Resource systemTemplate;

    public ChatController(ChatClient chatClient, @Qualifier("chatClientWithMemory") ChatClient chatClientWithMemory) {
        this.chatClient = chatClient;
        this.chatClientWithMemory = chatClientWithMemory;
    }


     

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt(message).call().content();
    }


    @GetMapping("/chatWithMemory")
    public String chat(@RequestParam String message, @RequestHeader String chatId) {
        return chatClientWithMemory.prompt()
        .advisors(spec -> spec.param("chatId", chatId))
        .user(message).call().content();
    }


    @GetMapping("/chatWithHRBot")
    public String chatWithHRBot(@RequestParam String message, @RequestHeader String chatId) {
        return chatClientWithMemory.prompt()
        .advisors(spec -> spec.param("chatId", chatId))
        .system(systemTemplate)
        .user(message).call().content();
    }

}
