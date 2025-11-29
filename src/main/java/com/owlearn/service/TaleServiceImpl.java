package com.owlearn.service;

import com.owlearn.dto.*;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.request.TaleOptionSearchRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.entity.Child;
import com.owlearn.entity.Quiz;
import com.owlearn.entity.Tale;
import com.owlearn.entity.User;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.ChildRepository;
import com.owlearn.repository.TaleRepository;import com.owlearn.repository.TaleReviewRepository;
import com.owlearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaleServiceImpl implements TaleService {

    private final TaleRepository taleRepository;
    private final RestTemplate restTemplate;
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final TaleReviewRepository taleReviewRepository;

    @Override
    public Long insertTale(TaleDto request) {
        Tale tale = Tale.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .type(Tale.TaleType.PREMADE)
                .child(null)
                .originTale(null)
                .subject(request.getSubject())
                .tone(request.getTone())
                .artStyle(request.getArtStyle())
                .ageGroup(request.getAgeGroup())
                .build();

        Tale saved = taleRepository.save(tale);
        return saved.getId();
    }

    @Override
    public List<TaleSummaryResponseDto> getAllTales() {
        return taleRepository.findAll().stream()
                .map(tale -> new TaleSummaryResponseDto(tale.getId(), tale.getTitle(), tale.getType().name(), null))
                .collect(Collectors.toList());
    }

    @Override
    public List<PremadeTaleResponseDto> getPremadeTales() {
        return taleRepository.findByType(Tale.TaleType.PREMADE).stream()
                .map(t -> new PremadeTaleResponseDto(
                        t.getId(),
                        t.getTitle(),
                        t.getType().name(),
                        t.getSubject(),
                        t.getTone(),
                        t.getArtStyle(),
                        t.getAgeGroup()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaleSummaryResponseDto> getUserGeneratedTales() {
        return taleRepository.findByType(Tale.TaleType.USER_GENERATED).stream()
                .map(t -> new TaleSummaryResponseDto(t.getId(), t.getTitle(), t.getType().name(), null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaleSummaryResponseDto> getUserGeneratedTalesByOptions(TaleOptionSearchRequestDto request) {
        List<Tale> candidateTales = taleRepository.findByTypeAndSubjectAndToneAndArtStyleAndAgeGroup(
                Tale.TaleType.USER_GENERATED,
                request.getSubject(),
                request.getTone(),
                request.getArtStyle(),
                request.getAgeGroup()
        );

        if (candidateTales.isEmpty()) {
            throw new ApiException(ErrorDefine.TALE_NOT_FOUND);
        }

        List<Long> taleIds = candidateTales.stream()
                .map(Tale::getId)
                .toList();

        List<Object[]> avgScores = taleReviewRepository.findAvgScoreByTaleIds(taleIds);

        Map<Long, Double> avgScoreMap = avgScores.stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Double) obj[1]
                ));

        List<Tale> topTales = candidateTales.stream()
                .sorted((t1, t2) -> Double.compare(
                        avgScoreMap.getOrDefault(t2.getId(), 0.0),
                        avgScoreMap.getOrDefault(t1.getId(), 0.0)
                ))
                .limit(3)
                .toList();

        return topTales.stream()
                .map(tale -> TaleSummaryResponseDto.builder()
                        .id(tale.getId())
                        .title(tale.getTitle())
                        .type(tale.getType().name())
                        .thumbnail(
                                tale.getImageUrls() != null && !tale.getImageUrls().isEmpty()
                                        ? tale.getImageUrls().get(0)
                                        : null
                        )
                        .build()
                )
                .toList();
    }


    @Override
    public TaleDto updateTale(Long taleId, TaleDto request) {
        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 동화가 존재하지 않습니다."));

        tale.setTitle(request.getTitle());
        tale.setContents(request.getContents());
        tale.setImageUrls(request.getImageUrls());

        Tale updated = taleRepository.save(tale);

        return TaleDto.builder()
                .title(updated.getTitle())
                .contents(updated.getContents())
                .imageUrls(updated.getImageUrls())
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

    @Override
    @Transactional(readOnly = true)
    public TaleSummaryResponseDto getRecentTaleByChildId(Long childId, String userId) {

        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        Optional<Tale> recentTaleOpt = taleRepository.findTopByChildIdOrderByCreatedAtDesc(childId);

        return recentTaleOpt
                .map(tale -> TaleSummaryResponseDto.builder()
                        .id(tale.getId())
                        .title(tale.getTitle())
                        .type(tale.getType().name())
                        .thumbnail(
                                tale.getImageUrls() != null && !tale.getImageUrls().isEmpty()
                                        ? tale.getImageUrls().get(0)
                                        : null
                        )
                        .build()
                )
                .orElse(null);
    }
}