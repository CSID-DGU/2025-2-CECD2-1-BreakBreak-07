import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import styles from "./LoginPage.module.css";
// import googleIcon from "../assets/google.png";
// import kakaoIcon from "../assets/kakao.png";
// import naverIcon from "../assets/naver.png";

import { signinAPI } from "../api/user";

function LoginPage() {
  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");

  useEffect(() => {
    console.log("입력 상태:", { id, pw });
  }, [id, pw]);

  const login = async () => {
    try {
      const response = await signinAPI(id, pw);
      console.log("로그인 응답:", response);

      const token = response?.data?.responseDto?.token;
      const role = response?.data?.responseDto?.role; // 서버에서 주는 role
      const name = response?.data?.responseDto?.name;

      if (token) {
        localStorage.setItem("token", token);
        localStorage.setItem("name", name);
        localStorage.setItem(
          "user",
          JSON.stringify({
            userId: id,
            role: role,
          })
        );

        // 관리자 여부 체크 -> 페이지 분기
        if (role === "ADMIN") {
          navigate("/admin");
        } else {
          navigate("/loginProfile");
        }
      }
    } catch (error) {
      console.error("로그인 중 오류 발생:", error);

      // 서버가 status를 반환한 경우
      if (error.response) {
        if (error.response.status === 401) {
          alert("아이디 또는 비밀번호가 올바르지 않습니다.");
        } else {
          alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
      } else {
        // 네트워크/서버 다운 등
        alert("네트워크 오류가 발생했습니다.");
      }
    }
  };

  const goToAdmin = () => {
    navigate("/admin");
  };

  return (
    <div className={styles.loginPage}>
      <div className={styles.loginBox}>
        <div className={styles.loginContent}>
          <h1 className={styles.loginTitle}>로그인</h1>

          {/* 흰색 박스: 아이디/비밀번호/버튼만 */}
          <input
            type="text"
            placeholder="아이디"
            className={styles.loginInput}
            value={id}
            onChange={(e) => setId(e.target.value)}
          />
          <input
            type="password"
            placeholder="비밀번호"
            className={styles.loginInput}
            value={pw}
            onChange={(e) => setPw(e.target.value)}
          />
          <button className={styles.loginButton} onClick={login}>
            로그인
          </button>
        </div>

        {/* 나머지 요소는 박스 밖에 그대로 */}
        <div className={styles.or}>
          {/* <hr className={styles.line} />
          <span className={styles.orText}>또는</span>
          <hr className={styles.line} /> */}
        </div>

        {/* <div className={styles.socialButtons}>
          <button className={styles.socialButton}>
            <img src={googleIcon} alt="Google" className={styles.socialIcon} />
          </button>
          <button className={styles.socialButton}>
            <img src={kakaoIcon} alt="Kakao" className={styles.socialIcon} />
          </button>
          <button className={styles.socialButton}>
            <img src={naverIcon} alt="Naver" className={styles.socialIcon} />
          </button>
        </div> */}

        <div className={styles.signup}>
          처음이신가요? <Link to="/register">회원가입</Link>
        </div>

        {/* <div className={styles.adminLink}>
          <button onClick={goToAdmin} className={styles.adminButton}>
            관리자 대시보드
          </button>
        </div> */}
      </div>
    </div>
  );
}

export default LoginPage;
