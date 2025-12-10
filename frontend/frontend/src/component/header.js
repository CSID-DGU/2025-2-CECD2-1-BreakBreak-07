import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./header.module.css";

function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem("user"); 
    localStorage.removeItem("userId");
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    localStorage.removeItem("selectedChild"); 
    navigate("/login");
  };

  const handleLogoClick = () => {
    // 부모 관리 영역에서는 항상 자녀 프로필 선택으로 유도
    if (location.pathname.startsWith("/parent")) {
      alert("학습할 자녀 프로필을 먼저 선택해주세요.");
      navigate("/loginProfile");
      return;
    }

    // 그 외에는 선택된 자녀가 있을 때만 학습 메인으로 이동
    const selectedChild = localStorage.getItem("selectedChild");
    if (!selectedChild) {
      alert("학습할 자녀 프로필을 먼저 선택해주세요.");
      navigate("/loginProfile");
      return;
    }

    navigate("/studyMain");
  };

  return (
    <div>
      <div className={styles.header}>
        <div>
          <button
            onClick={handleLogoClick}
            className={styles.logo}
          >
            OWLEARN
          </button>
        </div>

        <div className={styles.rightGroup}>
          <button className={styles.rightbtn} onClick={handleLogout}>
            Logout
          </button>
          <span>|</span>
          <button
            onClick={() => navigate(`/mypage`)}
            className={styles.rightbtn}
          >
            MyPage
          </button>
        </div>
      </div>
    </div>
  );
}

export default Header;
