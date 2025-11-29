package com.owlearn.service;

import com.owlearn.dto.request.*;
import com.owlearn.dto.response.*;
import com.owlearn.entity.Child;
import com.owlearn.entity.Tale;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.ChildRepository;
import com.owlearn.repository.TaleRepository;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ChildRepository childRepository;
    private final RestTemplate restTemplate;

    private static final String TEXT_ENDPOINT  = "http://localhost:8000/ai/text-generate";
    private static final String IMAGE_ENDPOINT = "http://localhost:8000/ai/image-generate";
    private static final String VOCAB_ENDPOINT = "http://localhost:8000/ai/vocab";
    private static final String RETELL_ENDPOINT = "http://localhost:8000/ai/retell";
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
    // 1) 기존 동화로 이미지 생성
    // =========================
    public TaleIdResponseDto generateImagesForExistingTale(String userId, ChildTaleRequestDto req) {
        Tale originalTale = taleRepository.findById(req.getTaleId())
                .orElseThrow(() -> new NoSuchElementException("동화가 존재하지 않습니다: id=" + req.getTaleId()));

        List<String> contents = originalTale.getContents();
        if (ObjectUtils.isEmpty(contents)) {
            throw new IllegalStateException("동화 내용(contents)이 비어 있습니다: id=" + req.getTaleId());
        }

        Child child = childRepository.findByIdAndUser_UserId(req.getChildId(), userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        String refImgUrl = Optional.ofNullable(child.getCharacterImageUrl())
                .filter(url -> !url.isBlank())
                .orElse(null);

        String artStyle = originalTale.getArtStyle();

        List<VocabResponseDto> vocabList = callVocabGenerate(contents);
        List<String> remoteUrls = callImageGenerate(contents, refImgUrl, artStyle);
        List<String> localUrls = ingestRemoteImages(remoteUrls);

        String title = originalTale.getTitle();
        if (title == null || title.isBlank()) {
            title = "Tale-" + originalTale.getId();
        }

        Tale newTale = Tale.builder()
                .child(child)
                .title(title)
                .contents(new ArrayList<>(originalTale.getContents()))
                .imageUrls(localUrls)
                .originTale(originalTale)
                .type(Tale.TaleType.FROM_PREMADE)
                .build();

        newTale = taleRepository.save(newTale);

        return TaleIdResponseDto.builder()
                .taleId(newTale.getId())
                .words(vocabList)
                .build();
    }

    // =========================
    // 2) 새 동화 생성 + 이미지 생성 (텍스트만 사용)
    // =========================
    public TaleIdResponseDto createTaleAndGenerateImages(String userId, TaleCreateRequestDto req) {

        TextGenerateRequestDto payload = TextGenerateRequestDto.builder()
                .subject(req.getSubject())
                .tone(req.getTone())
                .artStyle(req.getArtStyle())
                .ageGroup(req.getAgeGroup())
                .build();

        // FastAPI에서 동화 텍스트만 생성
        TextGenerateResponseDto text = callTextGenerate(payload);

        Child child = childRepository.findByIdAndUser_UserId(req.getChildId(), userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        // DB에 동화 저장
        Tale tale = Tale.builder()
                .child(child)
                .title(text.getTitle())
                .contents(text.getContents())
                .imageUrls(new ArrayList<>())
                .score(text.getScore())
                .subject(req.getSubject())
                .tone(req.getTone())
                .artStyle(req.getArtStyle())
                .ageGroup(req.getAgeGroup())
                .originTale(null)
                .type(Tale.TaleType.USER_GENERATED)
                .build();
        tale = taleRepository.save(tale);

        List<String> contents = text.getContents();
        String refImgUrl = Optional.ofNullable(child.getCharacterImageUrl())
                .filter(url -> !url.isBlank())
                .orElse(null);

        List<VocabResponseDto> vocabList = callVocabGenerate(contents);
        List<String> remoteUrls = callImageGenerate(contents, refImgUrl, req.getArtStyle());
        List<String> localUrls = ingestRemoteImages(remoteUrls);

        tale.setImageUrls(localUrls);
        taleRepository.save(tale);

        return TaleIdResponseDto.builder()
                .taleId(tale.getId())
                .reason(text.getReason())
                .words(vocabList)
                .build();
    }

    @Transactional
    public RetellEvalResponseDto evaluateRetelling(String userId, Long taleId, TaleRetellRequestDto req) {

        // 1) 자녀 검증
        Child child = childRepository.findByIdAndUser_UserId(req.getChildId(), userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        // 2) 동화 조회
        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new ApiException(ErrorDefine.TALE_NOT_FOUND));

        List<String> contents = tale.getContents();
        if (contents == null || contents.isEmpty()) {
            throw new IllegalStateException("동화 내용이 비어 있습니다.");
        }

        int idx = req.getSceneIndex();
        if (idx < 0 || idx >= contents.size()) {
            throw new IllegalArgumentException("유효하지 않은 장면 번호입니다: " + idx);
        }

        String originalScene = contents.get(idx);
        String userDescription = req.getUserDescription();

        // 3) FastAPI 리텔링 평가 요청
        RetellEvalResponseDto eval = callRetellEval(originalScene, userDescription);

        // 4) 크레딧 반영
        Integer credit = eval.getCredit();
        if (credit != null && credit > 0) {
            child.addCredit(credit);
            childRepository.save(child);
        }

        // 5) 프론트로는 피드백만 응답 (요구사항대로)
        return eval;
    }


    // ======== 내부 공통 ========
    private List<String> callImageGenerate(List<String> prompts, String refImgUrl, String artStyle) {
        ImageGenerateRequestDto payload = new ImageGenerateRequestDto(prompts, refImgUrl, artStyle);
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

    private List<VocabResponseDto> callVocabGenerate(List<String> contents) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", contents);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, jsonHeaders());

            ResponseEntity<List<VocabResponseDto>> resp = restTemplate.exchange(
                    URI.create(VOCAB_ENDPOINT),
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<VocabResponseDto>>() {}
            );

            List<VocabResponseDto> body = (resp != null) ? resp.getBody() : null;
            if (body == null) {
                // vocab 실패하면 빈 리스트로 반환
                return Collections.emptyList();
            }
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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

    private RetellEvalResponseDto callRetellEval(String originalScene, String userDescription) {

        Map<String, String> payload = new HashMap<>();
        payload.put("original_scene", originalScene);
        payload.put("user_description", userDescription);

        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(payload, jsonHeaders());

        ResponseEntity<RetellEvalResponseDto> resp = restTemplate.exchange(
                URI.create(RETELL_ENDPOINT),
                HttpMethod.POST,
                httpEntity,
                RetellEvalResponseDto.class
        );

        RetellEvalResponseDto body = (resp != null) ? resp.getBody() : null;
        if (body == null || body.getFeedback() == null || body.getCredit() == null) {
            throw new IllegalStateException("FastAPI 리텔링 평가 응답이 비정상입니다.");
        }
        return body;
    }

}
