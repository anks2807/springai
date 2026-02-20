package com.ttn.springai.service;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public void uploadDocument(MultipartFile file) throws IOException {
        List<Document> documents = extractDocuments(file);
        String documentId = UUID.randomUUID().toString();

         List<Document> chunks = documents.stream()
                .flatMap(doc -> textSplitter.split(doc).stream())
                .collect(Collectors.toList());

         for (Document chunk : chunks) {
             chunk.getMetadata().put("documentId", documentId);
             chunk.getMetadata().put("fileName", file.getOriginalFilename());
         }

        vectorStore.add(chunks);
    }

    public List<Document> extractDocuments(MultipartFile file) throws IOException {

        try (PDDocument pdf = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("fileName", file.getOriginalFilename());
            metadata.put("uploadedAt", Instant.now().toString());
            return List.of(new Document(text, metadata));
        }
    }

}
