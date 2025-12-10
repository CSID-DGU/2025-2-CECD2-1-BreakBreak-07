import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./addProfile.module.css";
import { addChildAPI } from "../api/user";

const ChildProfilePage = () => {
  const navigate = useNavigate();
  const [childName, setChildName] = useState("");
  const [birthdate, setBirthdate] = useState("");
  const [loading, setLoading] = useState(false);

  // 만나이 계산 함수 (생년월일 기반)
  const calculateAge = (birthdate) => {
    const birth = new Date(`${birthdate}T00:00:00`);
    if (Number.isNaN(birth.getTime())) {
      console.warn("[AgeCalc] 잘못된 생년월일 입력:", birthdate);
      return null;
    }

    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    const dayDiff = today.getDate() - birth.getDate();

    if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) {
      age -= 1;
    }

    return age;
  };

  // 입력 변화 시 콘솔로 확인
  useEffect(() => {
    const age = birthdate ? calculateAge(birthdate) : null;
    console.log("[AgeCalc] 입력 변경 확인:", {
      childName,
      birthdate,
      age,
    });
  }, [childName, birthdate]);

  const handleCreateProfile = async () => {
    if (!childName.trim()) {
      alert("자녀 이름을 입력해주세요.");
      return;
    }

    if (!birthdate) {
      alert("자녀의 생년월일을 선택해주세요.");
      return;
    }

    const age = calculateAge(birthdate);
    if (age === null || age < 0) {
      alert("생년월일이 올바르지 않습니다. 다시 확인해주세요.");
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    setLoading(true);

    try {
      const response = await addChildAPI({
        childName: childName,
        age,
      });

      console.log("addChild API 응답:", response);
      const childId = response?.childId || response?.data?.childId;
      if (!childId) {
        throw new Error("childId 누락");
      }

      navigate(`/diagnosis/${childId}`);
    } catch (error) {
      console.error("프로필 생성 오류:", error);
      alert("프로필 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <div className={styles.headerRow}>
          <div className={styles.logoChip}>🪐 OwlLearn Kids</div>
          <div className={styles.iconTray}>
            <span className={styles.iconBubble}>💛</span>
            <span className={styles.iconBubble}>🌈</span>
            <span className={styles.iconBubble}>🎈</span>
          </div>
        </div>

        <div className={styles.heroRow}>
          <div className={styles.heroText}>
            <h1 className={styles.title}>자녀 프로필 생성</h1>
            <p className={styles.subtitle}>
              아이 정보 입력으로 학습 여정을 시작해요.
            </p>
            <div className={styles.chipRow}>
              <span className={styles.chip}>🎉 만 나이 자동 계산</span>
              <span className={styles.chip}>📚 맞춤 도서 추천 준비</span>
            </div>
          </div>
          <div className={styles.mascotCard}>
            <div className={styles.mascotEmoji}>🦉</div>
            <p className={styles.mascotText}>
              만 나이로
              <br />
              단계를 추천해 드려요!
            </p>
          </div>
        </div>

        <div className={styles.formCard}>
          <div className={styles.form}>
            <div className={styles.formGroup}>
              <label>자녀 이름</label>
              <input
                placeholder="우리 아이 이름을 적어주세요"
                required
                className={styles.input}
                value={childName}
                onChange={(e) => setChildName(e.target.value)}
                disabled={loading}
              />
            </div>

            <div className={styles.formGroup}>
              <label>생년월일 (만 나이)</label>
              <input
                type="date"
                required
                className={styles.input}
                value={birthdate}
                onChange={(e) => setBirthdate(e.target.value)}
                disabled={loading}
              />
            </div>

            <button
              type="button"
              onClick={handleCreateProfile}
              className={styles.signupButton}
              disabled={loading}
            >
              {loading ? "생성 중..." : "프로필 생성"}
            </button>
          </div>
        </div>

        {/* <div className={styles.footerNote}>
          🪄 나이 기준으로 맞춤 학습을 안내해드려요.
        </div> */}
      </div>
    </div>
  );
};

export default ChildProfilePage;
