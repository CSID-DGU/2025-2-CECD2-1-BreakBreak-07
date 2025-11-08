package com.owlearn.service;

import com.owlearn.dto.request.ImageGenerateRequestDto;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.request.TextGenerateRequestDto;
import com.owlearn.dto.request.UserTaleRequestDto;
import com.owlearn.dto.response.ImageGenerateResponseDto;
import com.owlearn.dto.response.TaleIdResponseDto;
import com.owlearn.dto.response.TextGenerateResponseDto;
import com.owlearn.entity.Tale;
import com.owlearn.repository.TaleRepository;
import com.owlearn.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

/**
 * 1) 기존 동화 이미지 생성
 *    - taleId로 DB 조회 → 텍스트 목록 FastAPI 전달 → 이미지 생성 → 저장
 * 2) 새 동화 생성 + 이미지 생성
 *    - FastAPI에서 동화 텍스트만 생성 → 이미지 프롬프트 없이 텍스트 그대로 사용
 * 3) 공통
 *    - userId로 캐릭터 이미지 URL 조회해 refImgUrl로 전달
 */
@Service
@RequiredArgsConstructor
public class TaleAiService {

    private final TaleRepository taleRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String TEXT_ENDPOINT  = "http://localhost:8000/ai/text-generate";
    private static final String IMAGE_ENDPOINT = "http://localhost:8000/ai/image-generate";
    private static final String SAVE_DIR  = "/home/ubuntu/static/";
    private static final String PUB_PREFIX = "/images/";

    private Path saveDir;

    @PostConstruct
    void init() {
        saveDir = Paths.get(SAVE_DIR);
        try { Files.createDirectories(saveDir); }
        catch (IOException e) { throw new RuntimeException("이미지 저장 폴더 생성 실패: " + SAVE_DIR, e); }
    }

    // =========================
    // 1) 기존 동화에 이미지 생성
    // =========================
    public TaleIdResponseDto generateImagesForExistingTale(UserTaleRequestDto req) {
        Tale tale = taleRepository.findById(req.getTaleId())
                .orElseThrow(() -> new NoSuchElementException("동화가 존재하지 않습니다: id=" + req.getTaleId()));

        List<String> contents = tale.getContents();
        if (ObjectUtils.isEmpty(contents)) {
            throw new IllegalStateException("동화 내용(contents)이 비어 있습니다: id=" + req.getTaleId());
        }

        String refImgUrl = resolveUserCharacterImageUrl(req.getUserId());
        List<String> remoteUrls = callImageGenerate(contents, refImgUrl);
        List<String> localUrls = ingestRemoteImages(remoteUrls);

        tale.setImageUrls(localUrls);
        if (tale.getTitle() == null || tale.getTitle().isBlank()) {
            tale.setTitle("Tale-" + tale.getId());
        }
        taleRepository.save(tale);

        return TaleIdResponseDto.builder().taleId(tale.getId()).build();
    }

    // =========================
    // 2) 새 동화 생성 + 이미지 생성 (텍스트만 사용)
    // =========================
    public TaleIdResponseDto createTaleAndGenerateImages(TaleCreateRequestDto req) {

        TextGenerateRequestDto payload = TextGenerateRequestDto.builder()
                .subject(req.getSubject())
                .tone(req.getTone())
                .artStyle(req.getArtStyle())
                .ageGroup(req.getAgeGroup())
                .build();

        // FastAPI에서 동화 텍스트만 생성
        TextGenerateResponseDto text = callTextGenerate(payload);

        // DB에 동화 저장
        Tale tale = Tale.builder()
                .title(text.getTitle())
                .contents(text.getContents())
                .imageUrls(new ArrayList<>())
                .build();
        tale = taleRepository.save(tale);

        List<String> contents = text.getContents();
        String refImgUrl = resolveUserCharacterImageUrl(req.getUserId());

        List<String> remoteUrls = callImageGenerate(contents, refImgUrl);
        List<String> localUrls = ingestRemoteImages(remoteUrls);

        tale.setImageUrls(localUrls);
        taleRepository.save(tale);

        return TaleIdResponseDto.builder().taleId(tale.getId()).build();
    }

    // ======== 내부 공통 ========

    /** userId로 캐릭터 이미지 URL 조회 (없으면 null) */
    private String resolveUserCharacterImageUrl(Long userId) {
        return userRepository.findById(userId)
                .map(u -> {
                    String url = u.getCharacterImageUrl();
                    return (url != null && !url.isBlank()) ? url : null;
                })
                .orElse(null);
    }

    private List<String> callImageGenerate(List<String> prompts, String refImgUrl) {
        ImageGenerateRequestDto payload = new ImageGenerateRequestDto(prompts, refImgUrl);
        ResponseEntity<ImageGenerateResponseDto> resp = restTemplate.exchange(
                URI.create(IMAGE_ENDPOINT),
                HttpMethod.POST,
                new HttpEntity<>(payload, jsonHeaders()),
                ImageGenerateResponseDto.class
        );
        ImageGenerateResponseDto body = (resp != null) ? resp.getBody() : null;
        if (body == null || body.getUrls() == null || body.getUrls().isEmpty()) {
            throw new IllegalStateException("FastAPI가 유효한 이미지 URL을 반환하지 않았습니다.");
        }
        return body.getUrls();
    }

    private TextGenerateResponseDto callTextGenerate(TextGenerateRequestDto req) {
        ResponseEntity<TextGenerateResponseDto> resp = restTemplate.exchange(
                URI.create(TEXT_ENDPOINT),
                HttpMethod.POST,
                new HttpEntity<>(req, jsonHeaders()),
                TextGenerateResponseDto.class
        );
        TextGenerateResponseDto body = (resp != null) ? resp.getBody() : null;
        if (body == null || ObjectUtils.isEmpty(body.getContents())) {
            throw new IllegalStateException("FastAPI 텍스트 생성 실패 또는 비정상 응답");
        }
        return body;
    }

    /** 원격 URL들 다운로드 → 로컬 저장 → 공개 URL 목록 반환 */
    private List<String> ingestRemoteImages(List<String> remoteUrls) {
        List<String> results = new ArrayList<>();
        for (String url : remoteUrls) {
            byte[] bytes = download(url);
            if (bytes != null && bytes.length > 0) {
                results.add(saveImage(bytes, ".png"));
            }
        }
        if (results.isEmpty()) throw new IllegalStateException("다운로드/저장된 이미지가 없습니다.");
        return results;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private byte[] download(String url) {
        try { return restTemplate.getForObject(url, byte[].class); }
        catch (Exception e) { return null; }
    }

    private String saveImage(byte[] data, String ext) {
        if (data == null || data.length == 0) throw new IllegalArgumentException("빈 이미지 데이터");
        String filename = UUID.randomUUID() + (ext.startsWith(".") ? ext : "." + ext);
        Path path = saveDir.resolve(filename);
        try {
            Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return PUB_PREFIX + filename;
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 실패: " + filename, e);
        }
    }
}
