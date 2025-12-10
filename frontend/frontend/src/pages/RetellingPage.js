import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./RetellingPage.module.css";
import { getTale, retellAPI } from "../api/tale";
import { imageBaseUrl } from "../api/instance";
import creditIcon from "../assets/credit.png";

const RetellingPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const taleId = location.state?.taleId;

  const [taleTitle, setTaleTitle] = useState("");
  const [sceneImage, setSceneImage] = useState(null);
  const [sceneIndex, setSceneIndex] = useState(0);

  const [loading, setLoading] = useState(false);
  const [answer, setAnswer] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [selectedChild, setSelectedChild] = useState(null);

  // ⭐ 팝업 결과
  const [feedbackResult, setFeedbackResult] = useState(null);

  /* -------------------------------
      자녀 정보 로드
  -------------------------------- */
  useEffect(() => {
    const stored = localStorage.getItem("selectedChild");
    if (stored) {
      try {
        setSelectedChild(JSON.parse(stored));
      } catch (e) {
        console.error("selectedChild 파싱 실패:", e);
      }
    }
  }, []);

  /* -------------------------------
      동화 이미지 로드
  -------------------------------- */
  useEffect(() => {
    if (!taleId) return;

    let mounted = true;

    const fetchTale = async () => {
      try {
        setLoading(true);
        const res = await getTale(taleId);
        const dto = res.data?.responseDto || {};

        if (!mounted) return;

        setTaleTitle(dto.title || "");

        const images = Array.isArray(dto.imageUrls) ? dto.imageUrls : [];
        if (images.length > 0) {
          const randomIndex = Math.floor(Math.random() * images.length);
          setSceneIndex(randomIndex);

          const raw = images[randomIndex];
          setSceneImage(raw ? `${imageBaseUrl}${raw}` : null);
        }
      } catch (err) {
        console.error("리텔링용 동화 불러오기 실패:", err);
      } finally {
        if (mounted) setLoading(false);
      }
    };

    fetchTale();
    return () => {
      mounted = false;
    };
  }, [taleId]);

  /* -------------------------------
      문장 가독성 자동 분리 함수
  -------------------------------- */
  const formatFeedback = (text) => {
    if (!text) return "";

    // 💎 한국어 문장 분리 정확도 가장 높은 방식
    if (window.Intl && Intl.Segmenter) {
      const segmenter = new Intl.Segmenter("ko", { granularity: "sentence" });
      return [...segmenter.segment(text)]
        .map((s) => s.segment.trim())
        .filter((s) => s.length > 0)
        .join("<br/><br/>");
    }

    // fallback
    return text
      .split(/(?<=[.!?])\s+/)
      .map((s) => s.trim())
      .join("<br/><br/>");
  };

  /* -------------------------------
      리텔링 제출
  -------------------------------- */
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!answer.trim()) {
      alert("장면에서 어떤 일이 일어났는지 적어볼까요?");
      return;
    }

    if (!selectedChild?.id) {
      alert("학생 정보가 없습니다.");
      return;
    }

    setIsSubmitting(true);
    try {
      const res = await retellAPI(taleId, selectedChild.id, sceneIndex, answer);
      const dto = res.data?.responseDto;
      if (!dto) throw new Error("응답 없음");

      // 팝업 활성화
      setFeedbackResult({
        feedback: dto.feedback,
        credit: dto.credit,
        formatted: formatFeedback(dto.feedback),
      });
    } catch (err) {
      console.error("리텔링 제출 오류:", err);
      alert("리텔링 저장 중 오류가 발생했어요.");
    } finally {
      setIsSubmitting(false);
    }
  };

  /* -------------------------------
      리텔링 이후 다음 단계 선택
  -------------------------------- */
  const goReport = () => {
    navigate("/tale/report", { state: { taleId } });
  };

  const goFinish = () => {
    navigate("/studyMain");
  };

  return (
    <div className={styles.page}>
      <h1 className={styles.title}>이 장면, 다시 들려줄래요?</h1>
      <p className={styles.subtitle}>
        동화 속 장면을 떠올리며, 그때 어떤 일이 일어났는지{" "}
        <strong>내 말로 다시 써 보는</strong> 활동이에요.
      </p>

      <div className={styles.contentRow}>
        {/* ------------------ 좌측: 이미지 ------------------ */}
        <section className={styles.sceneCard}>
          <h2 className={styles.sceneTitle}>
            {taleTitle ? `「${taleTitle}」의 한 장면` : "리텔링할 장면"}
          </h2>

          <p className={styles.sceneHint}>
            아래 그림을 보고 등장인물·장소·사건 중심으로 적어보세요.
          </p>

          <div className={styles.sceneImageBox}>
            {loading && <p className={styles.sceneText}>장면 불러오는 중…</p>}

            {!loading && sceneImage && (
              <img src={sceneImage} alt="scene" className={styles.sceneImage} />
            )}

            {!loading && !sceneImage && (
              <p className={styles.sceneText}>
                이미지가 없는 동화예요. 기억나는 장면을 떠올려 적어보세요!
              </p>
            )}
          </div>
        </section>

        {/* ------------------ 우측: 입력 폼 ------------------ */}
        <form className={styles.formCard} onSubmit={handleSubmit}>
          <label className={styles.label}>
            이 장면에서 어떤 일이 있었나요?
          </label>

          <textarea
            className={styles.textarea}
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            placeholder="예: 돼지 삼형제가 각자 집을 지으며 준비하고 있었어요..."
          />

          <p className={styles.helper}>
            누가, 어디에서, 어떤 일을 했는지 순서대로 적어보면 좋아요.
          </p>

          <button
            type="submit"
            className={styles.primaryButton}
            disabled={isSubmitting}
          >
            {isSubmitting ? "제출 중..." : "리텔링 제출하기"}
          </button>
        </form>
      </div>

      {/* -------------------------------
          ⭐ 리텔링 결과 팝업
      -------------------------------- */}
      {feedbackResult && (
        <div className={styles.popupOverlay}>
          <div className={styles.popupCard}>
            <h2 className={styles.popupTitle}>리텔링 채점 결과</h2>

            {/* CREDIT */}
            <div className={styles.creditCenter}>
              <img src={creditIcon} className={styles.creditIcon} />
              <span className={styles.creditValue}>
                {feedbackResult.credit}C
              </span>
            </div>

            {/* FEEDBACK */}
            <div
              className={styles.feedbackText}
              dangerouslySetInnerHTML={{
                __html: feedbackResult.formatted,
              }}
            />

            {/* CREDIT EXPLAIN BOX */}
            <div className={styles.creditExplainBox}>
              💡 위 평가 기준에 따라 이번 리텔링은{" "}
              <strong>{feedbackResult.credit}C</strong>가 부여되었어요.
            </div>

            <div className={styles.popupActions}>
              <button
                type="button"
                className={styles.popupSecondary}
                onClick={goFinish}
              >
                학습 종료하기
              </button>
              <button
                type="button"
                className={styles.popupPrimary}
                onClick={goReport}
              >
                독후감 쓰기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RetellingPage;
