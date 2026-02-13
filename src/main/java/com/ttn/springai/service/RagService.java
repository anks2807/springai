package com.ttn.springai.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final VectorStore vectorStore;
    private final TextSplitter textSplitter;
    private final ChatClient chatClient;
    @Value("classpath:prompts/system-prompt-rag.st")
    Resource systemTemplate;

    public RagService(VectorStore vectorStore, TextSplitter textSplitter, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
        this.chatClient = chatClientBuilder.build();
    }

    public String ask(String question) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.builder().query(question).topK(3).build());
        String context = similarDocuments.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n"));
        Map<String, Object> params = Map.of("context", context, "question", question);
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemTemplate);
        return chatClient.prompt(systemPromptTemplate.create(params)).user(question).call().content();
    }


    public void ingest(String content) {
        List<Document> documents = textSplitter.split(new Document(content));
        vectorStore.add(documents);
    }

}
