package com.owlearn.service;

import com.owlearn.dto.request.TaleReviewCreateRequestDto;
import com.owlearn.dto.response.ReportSummaryDto;
import com.owlearn.dto.response.TaleReviewResponseDto;
import com.owlearn.entity.Child;
import com.owlearn.entity.Tale;
import com.owlearn.entity.TaleReview;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.ChildRepository;
import com.owlearn.repository.TaleRepository;
import com.owlearn.repository.TaleReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaleReviewService {

    private final TaleReviewRepository taleReviewRepository;
    private final ChildRepository childRepository;
    private final TaleRepository taleRepository;

    /**
     * 독후감 작성
     */
    public TaleReviewResponseDto createReview(String userId,
                                              Long childId,
                                              Long taleId,
                                              TaleReviewCreateRequestDto req) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        // child가 현재 로그인 유저의 소속인지 확인
        if (!child.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorDefine.ACCESS_DENIED);
        }

        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new ApiException(ErrorDefine.TALE_NOT_FOUND));

        TaleReview review = TaleReview.builder()
                .child(child)
                .tale(tale)
                .rating(req.getRating())
                .feeling(req.getFeeling())
                .build();

        TaleReview saved = taleReviewRepository.save(review);

        updateChildPrefer(childId);

        return toDto(saved);
    }

    /**
     * 특정 동화에 대한 독후감 리스트
     */
    public List<TaleReviewResponseDto> getReviewsByTale(Long taleId) {
        List<TaleReview> reviews = taleReviewRepository.findByTaleId(taleId);
        return reviews.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 특정 아이의 독후감 리스트
     */
    public List<TaleReviewResponseDto> getReviewsByChild(String userId, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        if (!child.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorDefine.ACCESS_DENIED);
        }

        List<TaleReview> reviews = taleReviewRepository.findByChildId(childId);
        return reviews.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 독후감 단건 조회
     */
    public TaleReviewResponseDto getReviewById(String userId, Long reviewId) {
        TaleReview review = taleReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ApiException(ErrorDefine.REVIEW_NOT_FOUND));

        // 이 리뷰가 로그인한 유저의 아이 것이 맞는지 검증
        if (!review.getChild().getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorDefine.ACCESS_DENIED);
        }

        return toDto(review);
    }

    private TaleReviewResponseDto toDto(TaleReview review) {

        return TaleReviewResponseDto.builder()
                .reviewId(review.getId())
                .childId(review.getChild().getId())
                .taleId(review.getTale().getId())
                .title(review.getTale().getTitle())
                .rating(review.getRating())
                .feeling(review.getFeeling())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportSummaryDto getReportSummaryByChildId(Long childId, String userId) {

        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        int count = taleReviewRepository.countByChildId(childId);

        return ReportSummaryDto.builder()
                .totalCount(count)
                .build();
    }

    private void updateChildPrefer(Long childId) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        // 1. Subject 옵션 업데이트
        // 해당 아이가 리뷰한 Subject 옵션 중 가장 높은 평균 평점을 가진 값(Value)을 찾습니다.
        String preferredSubject = findHighestRatedOption(
                taleReviewRepository.findAverageRatingBySubject(childId)
        );
        if (preferredSubject != null) {
            child.setPreferSubject(preferredSubject);
        }

        // 2. Tone 옵션 업데이트
        String preferredTone = findHighestRatedOption(
                taleReviewRepository.findAverageRatingByTone(childId)
        );
        if (preferredTone != null) {
            child.setPreferTone(preferredTone);
        }

        // 3. ArtStyle 옵션 업데이트
        String preferredArtStyle = findHighestRatedOption(
                taleReviewRepository.findAverageRatingByArtStyle(childId)
        );
        if (preferredArtStyle != null) {
            child.setPreferArtstyle(preferredArtStyle);
        }

        // 4. AgeGroup 옵션 업데이트
        String preferredAgeGroup = findHighestRatedOption(
                taleReviewRepository.findAverageRatingByAgeGroup(childId)
        );
        if (preferredAgeGroup != null) {
            child.setPreferAge(preferredAgeGroup);
        }

        // 데이터베이스에 변경 사항을 일괄 저장합니다.
        childRepository.save(child);
    }

    private String findHighestRatedOption(List<Object[]> ratingsAndOptions) {
        String bestOptionValue = null;
        Double maxRating = -1.0;

        for (Object[] result : ratingsAndOptions) {
            // result[0]: 옵션 값 (예: "과학"), result[1]: 평균 평점 (예: 4.5)
            String optionValue = (String) result[0];
            Double avgRating = ((Number) result[1]).doubleValue();

            if (avgRating > maxRating) {
                maxRating = avgRating;
                bestOptionValue = optionValue;
            }
        }

        // 가장 높은 평점을 가진 옵션 값(예: "공룡")을 반환합니다.
        return bestOptionValue;
    }
}
