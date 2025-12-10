import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./feedbackPage.module.css";
import { getUnknownWordsAPI } from "../api/child";

export default function FeedbackPage() {
  // API 데이터 상태 추가
  const [reviewWords, setReviewWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedWord, setSelectedWord] = useState(null);

  const navigate = useNavigate();
  const location = useLocation();
  const taleId = location.state?.taleId;

  const transmittedWords = location.state?.reviewWords;

  const goRetelling = () =>
    navigate("/tale/retelling", { state: { taleId } });

  // 데이터 로딩 및 초기화 로직
  useEffect(() => {
    const child = JSON.parse(localStorage.getItem("selectedChild"));
    const childId = child?.id;
    
    const fetchWords = async () => {
      setLoading(true);
      setError(null);
      if (!childId) {
        setError("아이디 정보를 찾을 수 없습니다.");
        setLoading(false);
        return;
      }

      try {
        const response = await getUnknownWordsAPI(childId);
        const wordsResponse = response.data?.responseDto;
        const words = Array.isArray(wordsResponse) ? wordsResponse : []; 
        
        setReviewWords(words);
        if (words.length > 0) {
          setSelectedWord(words[0]);
        }
      } catch (e) {
        console.error("단어 조회 실패:", e);
        setError("단어를 불러오는 중 문제가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    if (transmittedWords && transmittedWords.length > 0 && Array.isArray(transmittedWords)) {
        setReviewWords(transmittedWords);
        setSelectedWord(transmittedWords[0]);
        setLoading(false);
        return;
    }

    fetchWords();
  }, [location.state]); 

  if (loading) return <div className={styles.page}>단어 정보를 불러오는 중입니다...</div>;
  if (error) return <div className={styles.page}>{error}</div>;

  const hasWords = reviewWords.length > 0;

  return (
    <div className={styles.page}>
      <div className={styles.contentBox}>
        {/* 단어 카드 */}
        <section className={styles.wordContainer}>
          <div className={styles.wordHeader}>
            <h1 className={styles.pageTitle}>단어 복습하기</h1>

            <div className={styles.wordCount}>
              모르는 단어 {reviewWords.length}개 
            </div>
          </div>

          {hasWords ? (
            <>
              <div className={styles.wordTags}>
                {reviewWords.map((item, index) => (
                  <button
                    key={item.id || item.word || index}
                    type="button"
                    className={`${styles.tag} ${
                      selectedWord?.word === item.word ? styles.activeTag : ""
                    }`}
                    onClick={() => setSelectedWord(item)}
                  >
                    {item.word}
                  </button>
                ))}
              </div>

              {selectedWord && (
                <div className={styles.wordDetails}>
                  <div className={styles.wordName}>
                    {selectedWord.word}
                    <span className={styles.wordPos}>{selectedWord.pos}</span>
                  </div>

                  <div className={styles.wordMeaning}>
                    <span className={styles.wordLabel}>Meaning · </span>
                    {selectedWord.meaningEn}
                  </div>

                  <div className={styles.wordKor}>
                    <span className={styles.wordLabel}>한국어 뜻 · </span>
                    {selectedWord.meaningKo}
                  </div>

                  <div className={styles.wordExample}>
                    <span className={styles.wordLabel}>Example · </span>
                    {selectedWord.example}
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className={styles.noWordBox}>
              <p className={styles.noWordTitle}>모르는 단어가 없어요!</p>
              <p className={styles.noWordDesc}>
                이번 동화에서는 모르는 단어를 선택하지 않았어요.{" "}
                다음에 모르는 단어가 나오면 단어를 눌러서 함께 복습해볼까요?
              </p>
            </div>
          )}
        </section>

        {/* 다음 단계: 리텔링으로 이동 (필수) */}
        <div className={styles.actionButtons}>
          <button className={styles.brownBtn} onClick={goRetelling}>
            리텔링 하러 가기
          </button>
        </div>
      </div>
    </div>
  );
}
