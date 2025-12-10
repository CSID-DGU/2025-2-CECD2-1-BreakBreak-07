// StudyMain.jsx
import React, { useEffect, useState, useRef, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./studyMain.module.css";

import defaultAvatar from "../assets/myAvatar.png";
import book from "../assets/studyMainBook.png";
import { getOldTale, oldTaleImageGen } from "../api/tale";
import { imageBaseUrl } from "../api/instance";

import LoadingOverlay from "../component/LoadingOverlay";
import { getUnknownWordsAPI } from "../api/child";

const StudyMain = () => {
  const [child, setChild] = useState(null);
  const [recommendedTale, setRecommendedTale] = useState(null);

  const [loading, setLoading] = useState(false);
  const [loadingWord, setLoadingWord] = useState(null);
  const loadingIntervalRef = useRef(null);
  const [loadingWords, setLoadingWords] = useState([
    { word: "noUnknown", meaning: "모르는 단어 없음" },
    // { word: "imagine", meaning: "상상하다" },
    // { word: "curious", meaning: "호기심 많은" },
    // { word: "sparkle", meaning: "반짝이다" },
    // { word: "journey", meaning: "여정" },
    // { word: "wonder", meaning: "경이로움" },
    // { word: "dream", meaning: "꿈" },
    // { word: "explore", meaning: "탐험하다" },
  ]);

  const location = useLocation();
  const navigate = useNavigate();

  // ------------------- 로딩 단어 선택 -------------------
  const pickRandomWord = useCallback(() => {
    if (!loadingWords.length) {
      setLoadingWord(null);
      return;
    }
    const idx = Math.floor(Math.random() * loadingWords.length);
    setLoadingWord(loadingWords[idx]);
  }, [loadingWords]);

  // ------------------- 4초마다 영어단어 변경 -------------------
  useEffect(() => {
    if (loading) {
      pickRandomWord();
      loadingIntervalRef.current = setInterval(() => {
        pickRandomWord();
      }, 4000);
    } else {
      clearInterval(loadingIntervalRef.current);
      loadingIntervalRef.current = null;
      setLoadingWord(null);
    }

    return () => clearInterval(loadingIntervalRef.current);
  }, [loading, pickRandomWord]);

  // ------------------- 초기 child + 추천동화 -------------------
  useEffect(() => {
    const stored =
      location.state?.child ||
      JSON.parse(localStorage.getItem("selectedChild") || "null");

    if (stored) {
      setChild(stored);
      localStorage.setItem("selectedChild", JSON.stringify(stored));
    }

    const fetchTale = async () => {
      try {
        const res = await getOldTale();
        const list = res?.data.responseDto || [];
        if (list.length > 0) {
          setRecommendedTale(list[Math.floor(Math.random() * list.length)]);
        }
      } catch (e) {
        console.error("추천 동화 실패:", e);
      }
    };

    fetchTale();
  }, [location.state]);

  // ------------------- 자녀 모르는 단어 로딩 단어에 반영 -------------------
  useEffect(() => {
    const fetchUnknownWords = async () => {
      if (!child?.id) return;

      try {
        const res = await getUnknownWordsAPI(child.id);
        const dto = res.data?.responseDto;

        let wordsSource = [];
        if (Array.isArray(dto)) {
          wordsSource = dto;
        } else if (Array.isArray(dto?.words)) {
          wordsSource = dto.words;
        }

        const normalized = wordsSource
          .map((item) => {
            const word = item.word || item.text;
            const meaning =
              item.meaningKo || item.meaning || item.kor || item.meaningEn;
            return word
              ? {
                  word,
                  meaning: meaning || "",
                }
              : null;
          })
          .filter(Boolean);

        if (normalized.length > 0) {
          setLoadingWords(normalized);
        }
      } catch (e) {
        console.error("모르는 단어 로딩 단어 조회 실패:", e);
      }
    };

    fetchUnknownWords();
  }, [child]);

  const avatarSrc = child?.avatar
    ? `${imageBaseUrl}${child.avatar}`
    : defaultAvatar;

  // ------------------- 학습하기 -------------------
  const startReading = async () => {
    if (!recommendedTale || !child?.id) return;

    setLoading(true);

    try {
      const res = await oldTaleImageGen(recommendedTale.id, child.id);
      navigate("/tale/study", {
        state: { taleId: res.data.responseDto.taleId },
      });
    } catch (e) {
      console.error("동화 생성 실패:", e);
      alert("동화를 준비하는 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // ---------------------------------------------------
  // 🔥 렌더링
  // ---------------------------------------------------
  return (
    <div className={styles.page}>
      <div className={styles.grid}>
        {/* 왼쪽 */}
        <section className={styles.left}>
          <div className={styles.leftInner}>
            <img src={avatarSrc} className={styles.avatar} alt="avatar" />
            <div className={styles.childName}>{child?.name} 님</div>
            <div className={styles.ready}>✨ 오늘의 동화 준비 완료</div>
            <p className={styles.leftText}>오늘은 어떤 이야기를 만나볼까요?</p>
          </div>
        </section>

        {/* 오른쪽 */}
        <section className={styles.right}>
          <div className={styles.recommendCard}>
            <div className={styles.ribbon}>오늘의 추천 동화</div>

            <img src={book} className={styles.bookIcon} alt="book" />
            <h2 className={styles.taleTitle}>{recommendedTale?.title}</h2>

            <div className={styles.chipRow}>
              {recommendedTale?.subject && (
                <span className={styles.chip}>
                  <span className={styles.chipLabel}>주제</span>
                  {recommendedTale.subject}
                </span>
              )}
              {recommendedTale?.tone && (
                <span className={styles.chip}>
                  <span className={styles.chipLabel}>톤</span>
                  {recommendedTale.tone}
                </span>
              )}
              {recommendedTale?.artStyle && (
                <span className={styles.chip}>
                  <span className={styles.chipLabel}>화풍</span>
                  {recommendedTale.artStyle}
                </span>
              )}
              {recommendedTale?.ageGroup && (
                <span className={styles.chip}>
                  <span className={styles.chipLabel}>연령</span>
                  {recommendedTale.ageGroup}
                </span>
              )}
            </div>

            <button className={styles.readButton} onClick={startReading}>
              학습하기
            </button>
          </div>

          <div className={styles.customCard}>
            <div className={styles.customTitle}>맞춤 동화 만들기</div>
            <p className={styles.customDesc}>
              AI가 취향을 반영한 새로운 동화를 만들어줘요.
            </p>
            <button
              className={styles.customButton}
              onClick={() => navigate("/customStudy")}
            >
              만들기
            </button>
          </div>
        </section>

        {/* 🔥 프레임 내부 오버레이 */}
        {loading && (
          <div className={styles.innerOverlayWrapper}>
            <LoadingOverlay
              message="동화를 재구성중이에요…"
              subMessage="약 1분 정도 걸려요. 로딩되는 동안 모른다고 눌렀던 단어를 다시 학습해봅시다."
              word={loadingWord}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default StudyMain;
