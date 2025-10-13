package com.owlearn.service;

import com.owlearn.dto.request.GeminiRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Path saveDir;

    // FastAPI 엔드포인트
    private final String FastAPIUrl = "http://localhost:8000/ai/image-generate";

    public GeminiService() {
        // 로컬: src/main/resources/static/images
        // 배포 시: /home/ubuntu/static/
        this.saveDir = Paths.get("/home/ubuntu/static/");
        try {
            Files.createDirectories(this.saveDir);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 폴더 생성 실패", e);
        }
    }

    public List<String> generateImages(GeminiRequestDto request) throws IOException {
        String prompt = request.getPrompt();
        MultipartFile refImage = request.getRefImage();

        if (!StringUtils.hasText(prompt)) {
            throw new IllegalArgumentException("prompt is required");
        }
        if (refImage == null || refImage.isEmpty()) {
            throw new IllegalArgumentException("refImage is required");
        }

        // 1) 참조 이미지 저장 → 접근 URL 생성 (/images/xxx.png)
        String refImgUrl = saveImage(refImage.getBytes());

        // 2) 프롬프트 분할
        List<String> prompts = promptSplit(prompt);
        if (prompts.isEmpty()) {
            throw new IllegalArgumentException("Prompt could not be split into items.");
        }

        // 3) FastAPI 호출 (JSON: { prompts, refImgUrl })
        OrchestratorReq payload = new OrchestratorReq(prompts, refImgUrl);

        ResponseEntity<OrchestratorResp> resp = restTemplate.exchange(
                URI.create(FastAPIUrl),
                HttpMethod.POST,
                new HttpEntity<>(payload, defaultJsonHeaders()),
                OrchestratorResp.class
        );

        OrchestratorResp body = (resp != null) ? resp.getBody() : null;
        if (body == null || body.urls == null || body.urls.isEmpty()) {
            throw new IllegalStateException("FastAPI가 유효한 이미지 URL을 반환하지 않았습니다.");
        }

        // 4) 원격 URL들 → 바이트 다운로드 → 로컬 저장 → /images/... URL 반환
        List<byte[]> images = new ArrayList<>();
        for (String remoteUrl : body.urls) {
            byte[] bytes = downloadBytes(remoteUrl);
            if (bytes != null && bytes.length > 0) {
                images.add(bytes);
            }
        }

        List<String> imageUrls = new ArrayList<>();
        for (byte[] bytes : images) {
            imageUrls.add(saveImage(bytes));
        }

        return imageUrls;
    }

    private HttpHeaders defaultJsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    /** 원격 URL 이미지를 다운로드 */
    private byte[] downloadBytes(String url) {
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch (Exception e) {
            // 개별 실패는 스킵하고 다음 이미지 진행
            return null;
        }
    }

    /** 로컬에 이미지 저장하고 파일명 반환 */
    private String saveImage(byte[] image) {
        String filename = UUID.randomUUID() + ".png";
        Path path = saveDir.resolve(filename);
        try {
            Files.write(path, image, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 실패: " + filename, e);
        }

        return "/images/" + filename;
    }

    /** 번호(1. / 2) / 3 - ) 기반 분할 */
    private List<String> promptSplit(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        String normalized = "\n" + raw.strip(); // 첫 줄 번호 누락 대비
        String[] byNumber = normalized.split("(?m)^\\s*\\d+\\s*[\\.)-]\\s+");

        List<String> result = new ArrayList<>();
        for (String part : byNumber) {
            String text = part.strip();
            if (!text.isBlank()) result.add(text);
        }
        return result;
    }

    // ---- 내부 통신 DTO ----
    private record OrchestratorReq(List<String> prompts, String refImgUrl) {}
    private record OrchestratorResp(List<String> urls) {}
}
