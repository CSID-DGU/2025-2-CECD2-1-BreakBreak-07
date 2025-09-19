package com.owlearn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service
public class BananaService {

    private final WebClient http;
    private final String apiKey;
    private final String model;       // e.g. "gemini-2.5-flash-image-preview"
    private final long timeoutMs;
    private final ObjectMapper mapper = new ObjectMapper();

    public BananaService(
            @Value("${banana.endpoint}") String endpoint,
            @Value("${banana.api-key}") String apiKey,
            @Value("${banana.model}") String model,
            @Value("${banana.timeout-ms:15000}") long timeoutMs
    ) {
        this.http = WebClient.builder()
                .baseUrl(endpoint)                      // https://generativelanguage.googleapis.com/v1beta
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.apiKey = apiKey;
        this.model = model;
        this.timeoutMs = timeoutMs;
    }

    public byte[] generatePng(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt is required");
        }

        // 모델 ID 정규화
        String modelId = model.startsWith("models/") ? model : "models/" + model;
        String path = "/" + modelId + ":generateContent";

        // 옵션(사이즈/시드)은 현재 REST 파라미터 없으므로 프롬프트에 힌트로 삽입
        String effectivePrompt = buildEffectivePrompt(prompt);

        // 요청 바디 (문서 규격: contents.parts[].text)
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", effectivePrompt))
                ))
        );

        // 호출 (키는 헤더 x-goog-api-key)
        String raw = http.post()
                .uri(path)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(r -> r.bodyToMono(String.class).map(msg -> {
                    if (r.statusCode().isError()) {
                        throw new RuntimeException("Gemini " + r.statusCode() + " | " + msg);
                    }
                    return msg;
                }))
                .timeout(Duration.ofMillis(timeoutMs))
                .onErrorResume(e -> Mono.error(new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e)))
                .block();

        // 응답 파싱: candidates[].content.parts[].inline_data.data (base64 PNG)
        try {
            Map<?, ?> res = mapper.readValue(raw, Map.class);
            var candidates = (List<?>) res.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new IllegalStateException("candidates 비어있음: " + raw);
            }
            for (Object c : candidates) {
                Map<?, ?> cand = (Map<?, ?>) c;
                Map<?, ?> content = (Map<?, ?>) cand.get("content");
                if (content == null) continue;
                List<?> parts = (List<?>) content.get("parts");
                if (parts == null) continue;
                for (Object po : parts) {
                    Map<?, ?> part = (Map<?, ?>) po;
                    Map<?, ?> inline = (Map<?, ?>) part.get("inline_data");
                    if (inline == null) continue;
                    String mime = (String) inline.get("mime_type"); // "image/png" 등
                    String b64  = (String) inline.get("data");
                    if (mime != null && mime.startsWith("image/") && b64 != null && !b64.isBlank()) {
                        return Base64.getDecoder().decode(b64);
                    }
                }
            }
            throw new IllegalStateException("이미지 데이터가 없음: " + raw);
        } catch (Exception ex) {
            throw new RuntimeException("응답 파싱 실패: " + ex.getMessage(), ex);
        }
    }

    private static String buildEffectivePrompt(String prompt) {
        StringBuilder p = new StringBuilder(prompt.trim());
        p.append(" ");
        return p.toString();
    }
}