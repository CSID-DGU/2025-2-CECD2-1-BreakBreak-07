import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Prompting.module.css";
import { Image2ImageAPI } from "../api/prompt";
import { imageBaseUrl } from "../api/instance";

export default function Prompting() {
  const navigate = useNavigate();

  const [prompt, setPrompt] = useState("");
  const [refImage, setRefImage] = useState(null);
  const [imageUrls, setImageUrls] = useState([]);
  const [loading, setLoading] = useState(false);

  const sendButton = async (e) => {
    e.preventDefault();

    if (!prompt.trim()) {
      alert("프롬프트를 작성해주세요!");
      return;
    }

    try {
      setLoading(true);
      const response = await Image2ImageAPI(prompt, refImage);

      if (response && response.data) {
        setImageUrls(response.data);
        alert("동화 생성이 완료되었습니다!");
      } else {
        alert("동화 생성 결과가 비어 있습니다.");
      }
    } catch (error) {
      console.error("동화 생성 오류:", error);
      alert("동화 생성 중 문제가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.container}>
        <h2 className={styles.header}>나만의 동화 만들기</h2>

        <form className={styles.form} onSubmit={sendButton}>
          <div className={styles.row}>
            {/* 동화 입력 */}
            <section className={`${styles.card} ${styles.storyCard}`}>
              <h3 className={styles.cardTitle}>동화 입력</h3>
              <textarea
                className={styles.textarea}
                placeholder="프롬프트 입력"
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
              />
            </section>

            {/* 캐릭터 이미지 업로드 */}
            <section className={`${styles.card} ${styles.characterCard}`}>
              <h3 className={styles.cardTitle}>캐릭터 이미지 업로드</h3>

              <label className={styles.characterUploadBox}>
                {refImage ? (
                  <img
                    src={URL.createObjectURL(refImage)}
                    alt="캐릭터 미리보기"
                    className={styles.uploadPreview}
                  />
                ) : (
                  <>
                    <span>캐릭터</span>
                    <span>선택하기</span>
                  </>
                )}
                <input
                  type="file"
                  accept="image/*"
                  className={styles.characterFileInput}
                  onChange={(e) => setRefImage(e.target.files[0])}
                />
              </label>

              <div className={styles.characterFooter}>
                {/* <button
                  type="button"
                  className={styles.ghostBtn}
                  onClick={() => navigate("/studyMain")}
                >
                  돌아가기
                </button> */}

                <button
                  type="submit"
                  className={styles.primaryBtn}
                  disabled={loading}
                >
                  {loading ? "생성 중..." : "동화 만들기"}
                </button>
              </div>
            </section>
          </div>
        </form>

        {/* 생성된 이미지 */}
        <section className={styles.outputCard}>
          <h3 className={styles.cardTitle}>생성된 동화 이미지</h3>

          <div className={styles.outputGrid}>
            {loading ? (
              <p className={styles.noImage}>이미지 생성 중입니다...</p>
            ) : imageUrls.length > 0 ? (
              imageUrls.map((url, idx) => (
                <div key={idx} className={styles.outputItem}>
                  <img
                    src={`${imageBaseUrl}${url}`}
                    className={styles.illust}
                    alt={`동화 이미지 ${idx + 1}`}
                  />
                </div>
              ))
            ) : (
              <p className={styles.noImage}>아직 생성된 이미지가 없습니다.</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
