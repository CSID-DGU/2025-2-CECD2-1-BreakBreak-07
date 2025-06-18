package com.owlearn.service;

import com.owlearn.dto.*;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.response.TaleDetailResponseDto;
import com.owlearn.dto.response.TaleResponseDto;
import com.owlearn.dto.response.TaleSummaryResponseDto;
import com.owlearn.entity.Quiz;
import com.owlearn.entity.Tale;
import com.owlearn.repository.TaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaleServiceImpl implements TaleService {

    private final TaleRepository taleRepository;
    private final RestTemplate restTemplate;

    public TaleServiceImpl(TaleRepository taleRepository, RestTemplate restTemplate) {
        this.taleRepository = taleRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public TaleResponseDto createTale(TaleCreateRequestDto request) {
        // FastAPI 호출해서 동화 생성
        String fastApiUrl = "http://localhost:8000/api/tales";
        TaleDto apiResponse = restTemplate.postForObject(fastApiUrl, request, TaleDto.class);

        if (apiResponse == null) {
            throw new IllegalStateException("FastAPI 응답이 null입니다");
        }

        // DTO를 엔티티로 변환
        List<Quiz> quizzes = Optional.ofNullable(apiResponse.getQuizzes()).orElse(List.of()).stream()
                .map(dto -> Quiz.builder()
                        .questionNumber(dto.getQuestionNumber())
                        .question(dto.getQuestion())
                        .choices(dto.getChoices())
                        .answerIndex(dto.getAnswerIndex())
                        .explanation(dto.getExplanation())
                        .build())
                .collect(Collectors.toList());

        Tale tale = Tale.builder()
                .title(apiResponse.getTitle())
                .contents(apiResponse.getContents())
                .imageUrls(apiResponse.getImageUrls())
                .quizzes(quizzes)
                .build();

        // DB 저장된 내용 반환
        return TaleResponseDto.builder()
                .id(tale.getId())
                .build();
    }

    @Override
    public TaleDetailResponseDto getTale(Long taleId) {
        Tale tale = taleRepository.findById(taleId).orElseThrow();
        return new TaleDetailResponseDto(tale.getTitle(), tale.getContents(), tale.getImageUrls());
    }

    @Override
    public Long insertTale(TaleDto request) {
        Tale tale = Tale.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .imageUrls(request.getImageUrls())
                .quizzes(request.getQuizzes().stream()
                        .map(dto -> Quiz.builder()
                                .question(dto.getQuestion())
                                .choices(dto.getChoices())
                                .answerIndex(dto.getAnswerIndex())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        Tale saved = taleRepository.save(tale);
        return saved.getId();
    }

    @Override
    public List<TaleSummaryResponseDto> getAllTales() {
        return taleRepository.findAll().stream()
                .map(tale -> new TaleSummaryResponseDto(tale.getId(), tale.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public TaleDto updateTale(Long taleId, TaleDto request) {
        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 동화가 존재하지 않습니다."));

        tale.setTitle(request.getTitle());
        tale.setContents(request.getContents());
        tale.setImageUrls(request.getImageUrls());

        // 기존 퀴즈 제거 후 새로 설정
        tale.getQuizzes().clear();
        List<Quiz> newQuizzes = request.getQuizzes().stream()
                .map(dto -> Quiz.builder()
                        .questionNumber(dto.getQuestionNumber())
                        .question(dto.getQuestion())
                        .choices(dto.getChoices())
                        .answerIndex(dto.getAnswerIndex())
                        .explanation(dto.getExplanation())
                        .build())
                .toList();
        tale.getQuizzes().addAll(newQuizzes);

        Tale updated = taleRepository.save(tale);

        return TaleDto.builder()
                .title(updated.getTitle())
                .contents(updated.getContents())
                .imageUrls(updated.getImageUrls())
                .quizzes(updated.getQuizzes().stream()
                        .map(q -> QuizDto.builder()
                                .questionNumber(q.getQuestionNumber())
                                .question(q.getQuestion())
                                .choices(q.getChoices())
                                .answerIndex(q.getAnswerIndex())
                                .explanation(q.getExplanation())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void deleteTale(Long taleId) {
        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 동화가 존재하지 않습니다."));

        // 이미지 파일 삭제
        List<String> imageUrls = tale.getImageUrls();
        for (String imageUrl : imageUrls) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path path = Paths.get("/home/ubuntu/static/", fileName);

            try {
                Files.deleteIfExists(path); // 존재할 때만 삭제
            } catch (IOException e) {
                System.err.println("파일 삭제 실패: " + path + " - " + e.getMessage());
            }
        }

        taleRepository.delete(tale);
    }

    @Override
    public List<String> saveImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        String uploadDir = "/home/ubuntu/static/";; // 프로젝트 외부 디렉토리

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs(); // 폴더가 없으면 생성
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;

            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // UUID로 유니크한 파일명 생성 (중복방지)
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            try {
                File dest = new File(uploadDir + storedFileName);
                image.transferTo(dest); // 파일 저장

                // 외부 디렉토리 매핑 경로에 맞게 URL 생성
                String imageUrl = "/images/" + storedFileName;
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imageUrls;
    }

}