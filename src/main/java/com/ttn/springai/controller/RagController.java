package com.ttn.springai.controller;

import com.ttn.springai.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class RagController {

    private final RagService ragService;
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return ragService.ask(question);
    }



    @PostMapping("/ingest")
    public String ingest(@RequestBody String content) {
        ragService.ingest(content);
        return "Success!";
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {

        ragService.uploadDocument(file);

        return ResponseEntity.ok(Map.of("message", "File uploaded and processed successfully"));
    }
}
