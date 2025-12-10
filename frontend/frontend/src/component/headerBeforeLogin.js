import React from "react";
import { useNavigate } from "react-router-dom";
import styles from "./headerBeforeLogin.module.css";

function Header() {
  const navigate = useNavigate();

  return (
    <div>
      <div className={styles.header}>
        <div>
          <button
            onClick={() => navigate(`/studyMain`)}
            className={styles.logo}
          >
            OWLEARN
          </button>
        </div>

        <div className={styles.rightGroup}>
          {/* <button className={styles.rightbtn}>Logout</button>
          <span>|</span>
          <button
            // onClick={() => navigate(`/mypage`)}
            // //마이페이지 구현 후 활성화
            className={styles.rightbtn}
          >
            MyPage
          </button> */}
        </div>
      </div>
    </div>
  );
}

export default Header;
