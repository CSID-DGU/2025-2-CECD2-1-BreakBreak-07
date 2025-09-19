package com.owlearn.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateImagesConfig;
import com.google.genai.types.GenerateImagesResponse;
import com.google.genai.types.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GeminiService {

    private final Client client;
    private final String imageModel; // e.g. "imagen-3.0-generate-002"
    private final long timeoutMs;
    private final Path saveDir;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.image-model}") String imageModel,
            @Value("${gemini.timeout-ms:15000}") long timeoutMs
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)          // Gemini Developer API 사용
                .build();
        this.imageModel = imageModel;
        this.timeoutMs = timeoutMs;
        this.saveDir = Paths.get("/home/ubuntu/static/"); // 배포시
        // this.saveDir = Paths.get("src/main/resources/static/images"); // 로컬
        try {
            Files.createDirectories(this.saveDir);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 폴더 생성 실패", e);
        }
    }

    public List<String> generateImages(String prompt) {
        if (prompt == null || prompt.isBlank()) throw new IllegalArgumentException("prompt is required");

        GenerateImagesConfig cfg = GenerateImagesConfig.builder()
                .outputMimeType("image/png")
                .includeSafetyAttributes(true)
                .build();

        GenerateImagesResponse res = client.models.generateImages(imageModel, prompt, cfg);

        List<byte[]> images = res.generatedImages()
                .map(list -> list.stream()
                        .map(imgResp -> imgResp.image()
                                .flatMap(Image::imageBytes)
                                .orElse(null))
                        .filter(b -> b != null && b.length > 0)
                        .toList())
                .orElse(List.of());

        List<String> urls = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            String filename = UUID.randomUUID() + ".png";
            Path path = saveDir.resolve(filename);
            try {
                Files.write(path, images.get(i));
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패: " + filename, e);
            }
            urls.add("/images/" + filename);
        }
        return urls;
    }
}