import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import styles from "./reportPage.module.css";
import { getReviewDetailAPI } from "../api/review";
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

const ReviewDetail = () => {
  const navigate = useNavigate();
  const { reviewId } = useParams();
  const location = useLocation();
  const search = new URLSearchParams(location.search);
  const from = search.get("from");

  const [loading, setLoading] = useState(true);
  const [review, setReview] = useState(null);
  const [taleTitle, setTaleTitle] = useState("");

  useEffect(() => {
    const fetchReview = async () => {
      try {
        const json = await getReviewDetailAPI(reviewId);
        console.log("최종 파싱된 상세 데이터:", json);

        setReview(json);

        if (from === "parent") {
          setTaleTitle("알 수 없는 동화");
          return;
        }

        try {
          const taleJson = await getTale(json.taleId);
          setTaleTitle(taleJson.data.responseDto?.title || "알 수 없는 동화");
        } catch {
          setTaleTitle("알 수 없는 동화");
          return;
        }
      } catch (err) {
        console.error("리뷰 조회 실패:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchReview();
  }, [reviewId, from]);

  if (loading) return <div className={styles.page}>불러오는 중...</div>;
  if (!review)
    return <div className={styles.page}>리뷰를 찾을 수 없습니다.</div>;

  const feelings = Array.isArray(review.feeling)
    ? review.feeling
    : typeof review.feeling === "string"
    ? review.feeling
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean)
    : [];

  return (
    <div className={styles.page}>
      <button className={styles.backButton} onClick={() => navigate(-1)}>
        ← 뒤로 가기
      </button>

      <header className={styles.hero}>
        <div className={styles.heroText}>
          <p className={styles.eyebrow}>독후감 조회</p>
          <h1 className={styles.heroTitle}>
            {review.title || taleTitle || "알 수 없는 동화"}
          </h1>
        </div>
      </header>

      <div className={styles.form}>
        {/* 감정 태그 */}
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h3>읽고 난 지금의 기분</h3>
          </div>

          <div className={styles.tagList}>
            {emotionTags.map(({ label, tone }) => (
              <span
                key={label}
                className={`${styles.tagButton} ${styles[`tone-${tone}`]} ${
                  feelings.includes(label) ? styles.active : ""
                }`}
              >
                {label}
              </span>
            ))}
          </div>
        </section>

        {/* 별점 */}
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h3>동화 리뷰</h3>
          </div>

          <div className={styles.ratingRow}>
            <div className={styles.stars}>
              {[1, 2, 3, 4, 5].map((value) => (
                <span
                  key={value}
                  className={
                    value <= review.rating
                      ? `${styles.star} ${styles.starActive}`
                      : styles.star
                  }
                >
                  ★
                </span>
              ))}
            </div>
            <span className={styles.ratingValue}>{review.rating} / 5</span>
          </div>
        </section>
      </div>
    </div>
  );
};

export default ReviewDetail;
