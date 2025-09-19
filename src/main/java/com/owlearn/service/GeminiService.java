package com.owlearn.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateImagesConfig;
import com.google.genai.types.GenerateImagesResponse;
import com.google.genai.types.*;
import com.owlearn.dto.request.GeminiRequestDto;
import org.springframework.web.multipart.MultipartFile;
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
    private final Path saveDir;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.image-model}") String imageModel
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)          // Gemini Developer API 사용
                .build();
        this.imageModel = imageModel;
        this.saveDir = Paths.get("/home/ubuntu/static/"); // 배포시
        // this.saveDir = Paths.get("src/main/resources/static/images"); // 로컬
        try {
            Files.createDirectories(this.saveDir);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 폴더 생성 실패", e);
        }
    }

    public List<String> generateImages(GeminiRequestDto request) {

        String prompt = request.getPrompt();
        MultipartFile refImage = request.getRefImage();

        if (prompt == null || prompt.isBlank()) throw new IllegalArgumentException("prompt is required");

        List<byte[]> images = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        if(refImage == null){
            images = text2img(prompt);
        }
        else{
            images = img2img(prompt, refImage);
        }

        urls = saveImages(images);

        return urls;
    }


    private List<byte[]> text2img(String prompt) {
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

        return images;
    }

    private List<byte[]> img2img(String prompt, MultipartFile refImage) {

        return null;
    }

    private List<String> saveImages(List<byte[]> images) {
        List<String> urls = new ArrayList<>();
        for (byte[] data : images) {
            String filename = UUID.randomUUID() + ".png";
            Path path = saveDir.resolve(filename);
            try {
                Files.write(path, data);
            } catch (IOException e) {
                throw new IllegalStateException("이미지 저장 실패: " + filename, e);
            }
            urls.add("/images/" + filename);
        }
        return urls;
    }
}