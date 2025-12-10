import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./reportPage.module.css";
import { submitReportAPI } from "../api/report";
import { getTale } from "../api/tale";

const emotionTags = [
  { label: "재미있었어요", tone: "lavender" },
  { label: "감동적이었어요", tone: "rose" },
  { label: "배울 게 많았어요", tone: "mint" },
  { label: "생각이 많아졌어요", tone: "sky" },
  { label: "다시 읽고 싶어요", tone: "sunset" },
  { label: "지루했어요", tone: "ash" },
  { label: "슬펐어요", tone: "teal" },
  { label: "어려웠어요", tone: "sand" },
];

const ReadingReflection = () => {
  const navigate = useNavigate();
  const { state } = useLocation();
  const taleId = state?.taleId;

  const [selectedChild, setSelectedChild] = useState(null);
  const [storyInfo, setStoryInfo] = useState({
    title: state?.title || "",
  });
  const [storyLoading, setStoryLoading] = useState(false);

  useEffect(() => {
    try {
      const stored = localStorage.getItem("selectedChild");
      setSelectedChild(stored ? JSON.parse(stored) : null);
    } catch (error) {
      console.error("선택된 아이 정보 파싱 실패:", error);
      setSelectedChild(null);
    }
  }, []);

  useEffect(() => {
    let mounted = true;

    const fetchStory = async () => {
      if (!taleId) return;
      setStoryLoading(true);
      try {
        //동화 제목 가져옴
        const response = await getTale(taleId);
        const dto = response.data?.responseDto || {};
        if (!mounted) return;
        setStoryInfo({
          title: dto.title || "알 수 없는 동화",
        });
      } catch (error) {
        console.error("동화를 불러오지 못했습니다:", error);
      } finally {
        if (mounted) setStoryLoading(false);
      }
    };

    fetchStory();
    return () => {
      mounted = false;
    };
  }, [taleId]);

  // 입력 상태값
  const [selectedTags, setSelectedTags] = useState([]);
  const [rating, setRating] = useState(4);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [payload, setPayload] = useState(null);

  //기분 태그 선택 및 해제
  const toggleTag = (tag) => {
    setSelectedTags((prev) =>
      prev.includes(tag) ? prev.filter((item) => item !== tag) : [...prev, tag]
    );
  };

  useEffect(() => {
    if (!taleId || !selectedChild?.id) {
      setPayload(null);
      return;
    }

    setPayload({
      childId: selectedChild.id,
      taleId,
      rating,
      feeling: selectedTags,
    });
  }, [selectedChild?.id, taleId, rating, selectedTags]);

  useEffect(() => {
    if (!payload) return;
    console.log("[payload]", payload);
  }, [payload]);

  //독후감 제출
  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!taleId) return alert("동화 정보 오류");
    if (!selectedChild?.id) return alert("학생 정보 오류");

    const requestPayload = payload || {
      childId: selectedChild.id,
      taleId,
      rating,
      feeling: selectedTags,
    };

    console.log("[입력]", requestPayload);

    try {
      setIsSubmitting(true);

      await submitReportAPI(requestPayload);

      alert("독후감이 저장되었습니다!");
      navigate("/studyMain");
    } catch (error) {
      console.error("독후감 저장 실패:", error);
      alert("독후감 저장에 실패했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <h1 className={styles.heroTitle}>
        오늘 읽은 동화를 나만의 언어로 기록해요
      </h1>

      <div className={styles.storyCard}>
        <span className={styles.badge}>현재 작성 중</span>
        <h2>{storyInfo.title || "동화 정보를 불러오는 중..."}</h2>
        {/* {storyInfo.mood && <p className={styles.meta}>{storyInfo.mood}</p>}
          {storyInfo.summary && (
            <p className={styles.summary}>{storyInfo.summary}</p>
          )} */}
        {storyLoading && (
          <p className={styles.meta}>동화 정보를 불러오고 있어요...</p>
        )}
      </div>

      <form className={styles.form} onSubmit={handleSubmit}>
        {/* 감정 태그 */}
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h3>읽고 난 지금의 기분</h3>
            <p>가장 가까운 느낌을 골라보세요. 여러 개 선택 가능해요.</p>
          </div>

          <div className={styles.tagList}>
            {emotionTags.map((tag) => (
              <button
                key={tag.label}
                type="button"
                className={`${styles.tagButton} ${styles[`tone-${tag.tone}`]} ${
                  selectedTags.includes(tag.label) ? styles.active : ""
                }`}
                onClick={() => toggleTag(tag.label)}
              >
                {tag.label}
              </button>
            ))}
          </div>
        </section>

        {/* 별점 */}
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h3>동화 리뷰</h3>
            <p>오늘의 동화를 별점으로 평가해볼까요?</p>
          </div>

          <div className={styles.ratingRow}>
            <div className={styles.stars}>
              {[1, 2, 3, 4, 5].map((value) => (
                <button
                  key={value}
                  type="button"
                  className={
                    value <= rating
                      ? `${styles.star} ${styles.starActive}`
                      : styles.star
                  }
                  onClick={() => {
                    setRating(value);
                    console.log("별점 선택:", value);
                  }}
                  aria-label={`${value}점`}
                >
                  ★
                </button>
              ))}
            </div>
            <span className={styles.ratingValue}>{rating} / 5</span>
          </div>
        </section>

        {/* 제출 버튼 */}
        <div className={styles.submitRow}>
          <button
            type="submit"
            className={styles.ghostButton}
            disabled={isSubmitting}
          >
            {isSubmitting ? "저장 중..." : "독후감 저장하기"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ReadingReflection;
