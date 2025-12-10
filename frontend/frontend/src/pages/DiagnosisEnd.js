import React, { useEffect, useState } from "react";
import styles from "./DiagnosisEnd.module.css";
import { useNavigate, useLocation } from "react-router-dom";
import { getCharacterAPI } from "../api/user";

const DiagnosisEnd = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const receivedUrl = location.state?.imageUrl;
  const BASE_URL = process.env.REACT_APP_URL; // 배포 도메인

  const [imageUrl, setImageUrl] = useState(
    receivedUrl || sessionStorage.getItem("imageUrl") || ""
  );

  useEffect(() => {
    if (receivedUrl) {
      sessionStorage.setItem("imageUrl", receivedUrl);
      setImageUrl(receivedUrl);
    }
  }, [receivedUrl]);

  console.log("이미지 URL:", imageUrl);

  const onClick = async () => {
    try {
      const childId = sessionStorage.getItem("childId"); // childId 사용 경로에 맞게 조정 가능

      // ⭐ 최신 캐릭터 정보 조회
      const res = await getCharacterAPI(childId);
      console.log("🔄 최신 캐릭터 조회 성공:", res.data.responseDto);

      // ⭐ 조회 성공한 후에만 프로필 선택 화면으로 이동
      navigate("/loginProfile");
    } catch (err) {
      console.error("캐릭터 조회 실패:", err);
      // 실패해도 일단 이동은 가능하게 유지
      navigate("/loginProfile");
    }
  };

  return (
    <div className={styles.diagnosisEnd}>
      {/* 좌우에 불꽃 이미지 배치 */}

      <div className={styles.card}>
        <div className={styles.badge}>프로필 준비 완료</div>
        <h1 className={styles.title}>완성되었습니다!</h1>
        <p className={styles.subtitle}>만든 아바타로 학습을 이어가 볼까요?</p>

        <div className={styles.avatarBox}>
          {imageUrl && (
            <img
              src={`${BASE_URL}${imageUrl}`}
              alt="완성된 아바타"
              className={styles.avatarImg}
            />
          )}
        </div>

        <button className={styles.button} onClick={onClick}>
          프로필 선택으로 이동
        </button>
      </div>
    </div>
  );
};

export default DiagnosisEnd;
