import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./taleExplain.module.css";
import { getTale } from "../api/tale";

export default function TaleExplain() {
  const { state } = useLocation();
  const navigate = useNavigate();

  //  title 상태 추가
  const [title, setTitle] = useState(state?.title || "");

  // 🔥 state가 undefined일 수도 있으므로 optional chaining
  const taleId = state?.taleId;
  const selections = state?.selections;
  const reason = state?.reason;
  const story = state?.story;

  // 🔥 taleId 기반으로 상세 조회 API 호출
  useEffect(() => {
    if (!taleId) return;

    getTale(taleId)
      .then((res) => {
        console.log("동화 상세:", res.data);
        setTitle(res.data.responseDto.title);
      })
      .catch((err) => console.error("제목 불러오기 실패:", err));
  }, [taleId]);

  if (!state) return <p>잘못된 접근입니다.</p>;

  const handleGoStudy = () => {
    navigate("/tale/study", {
      state: {
        taleId,
        title,
        story,
      },
    });
  };

  return (
    <div className={styles.explainWrapper}>
      {/* 페이지 제목 */}
      <div className={styles.explainTitle}>
        <h1>동화 생성이 완료되었어요!</h1>
        <p>이번 동화가 어떻게 만들어졌는지 확인해볼까요?</p>
      </div>

      <div className={styles.explainCard}>
        {/* 동화 제목 */}
        <div className={styles.storyTitleBox}>
          <h2 className={styles.storyTitle}>
            {title || "제목 불러오는 중..."}
          </h2>
          <span className={styles.storyBadge}>AI 맞춤 생성</span>
        </div>

        {/* 가로 2박스 */}
        <div className={styles.explainFlex}>
          {/* 선택한 옵션 */}
          <div className={styles.explainBox}>
            <h3>🍀 선택한 옵션</h3>

            <div className={styles.optionGrid}>
              <div className={styles.optionItem}>
                <span className={styles.optionLabel}>주제</span>
                <span className={styles.optionSep}> | </span>
                <span className={styles.optionValue}>{selections?.theme}</span>
              </div>

              <div className={styles.optionItem}>
                <span className={styles.optionLabel}>분위기</span>
                <span className={styles.optionSep}> | </span>
                <span className={styles.optionValue}>{selections?.mood}</span>
              </div>

              <div className={styles.optionItem}>
                <span className={styles.optionLabel}>그림체</span>
                <span className={styles.optionSep}> | </span>
                <span className={styles.optionValue}>
                  {selections?.artStyle}
                </span>
              </div>

              <div className={styles.optionItem}>
                <span className={styles.optionLabel}>연령대</span>
                <span className={styles.optionSep}> | </span>
                <span className={styles.optionValue}>
                  {selections?.ageGroup}
                </span>
              </div>
            </div>
          </div>

          {/* 생성 이유 */}
          <div className={styles.explainBox}>
            <h3>✨ 이렇게 생성되었어요</h3>

            <p className={styles.reasonItem}>{reason}</p>
          </div>
        </div>

        {/* 버튼 */}
        <button className={styles.explainButton} onClick={handleGoStudy}>
          학습 시작하기
        </button>
      </div>
    </div>
  );
}
