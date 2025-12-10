import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./register.module.css";

import { signupAPI, idCheckAPI, signinAPI } from "../api/user";

const SignupPage = () => {
  const navigate = useNavigate();

  const [userId, setUserId] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [isDuplicateChecked, setIsDuplicateChecked] = useState(false);
  const [idCheckMessage, setIdCheckMessage] = useState("");
  const [idCheckStatus, setIdCheckStatus] = useState("idle"); // idle | checking | available | taken | error

  useEffect(() => {
    console.log("입력 상태:", { userId, name, password });
  }, [userId, name, password]);

  const handleSignup = async (e) => {
    e.preventDefault();

    if (!userId.trim() || !name.trim() || !password.trim()) {
      alert("모든 항목을 빠짐없이 입력해주세요.");
      return;
    }

    if (!isDuplicateChecked) {
      alert("아이디 중복 확인을 먼저 해주세요.");
      return;
    }

    try {
      // 1) 회원가입 요청
      const response = await signupAPI(userId, name, password);
      console.log("회원가입 성공:", response);

      alert("회원가입이 완료되었습니다!");
      navigate("/login");

      /* 2) 자동 로그인 시도
      const loginResponse = await signinAPI(userId, password);

      console.log("자동 로그인 응답:", loginResponse);

      const token = loginResponse?.data?.responseDto?.token;

      if (!token) {
        alert("자동 로그인에 실패했습니다. 로그인 페이지로 이동합니다.");
        navigate("/login");
        return;
      }

      // 3) 토큰 저장
      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);

      // 4) 학생추가 페이지로 이동
      navigate("/loginProfile");*/

    } catch (error) {
      console.error("회원가입 실패:", error);
      alert("회원가입에 실패했습니다. 다시 시도해주세요.");
    }
  };

    const checkDuplicateId = async () => {
      // 입력이 비었으면 안내만 표시하고 종료
      if (!userId.trim()) {
        setIdCheckMessage("아이디를 입력해주세요.");
        setIdCheckStatus("error");
        setIsDuplicateChecked(false);
        return;
      }

      try {
        setIdCheckStatus("checking");
        const response = await idCheckAPI(userId); // API 비동기처리
        console.log("중복 확인 응답:", response);

        const message = response?.data?.responseDto?.message;

        if (message) {
          const available = message === "사용가능한 아이디입니다.";
          setIsDuplicateChecked(available);
          setIdCheckMessage(message);
          setIdCheckStatus(available ? "available" : "taken");
        } else {
          setIdCheckMessage("서버로부터 메시지를 받지 못했습니다.");
          setIdCheckStatus("error");
          setIsDuplicateChecked(false);
        }
      } catch (error) {
        console.error("중복 확인 오류:", error);
        setIdCheckMessage("중복 확인 중 오류가 발생했습니다.");
        setIdCheckStatus("error");
        setIsDuplicateChecked(false);
      }
  };

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <h1 className={styles.title}>어서오세요!</h1>
        <p className={styles.subtitle}>
          간단한 정보를 입력하고 서비스를 시작하세요
        </p>

        <form className={styles.form} onSubmit={handleSignup}>
          <div className={styles.formGroup}>
            <label>학부모 이름</label>
            <input
              placeholder="이름을 입력하세요"
              required
              className={styles.input}
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className={styles.formGroup}>
            <label>아이디</label>
            <div className={styles.inputWithButton}>
              <input
                placeholder="아이디를 입력하세요"
                required
                className={styles.input}
                value={userId}
                onChange={(e) => {
                  setUserId(e.target.value);
                  setIsDuplicateChecked(false); // 입력값이 바뀌면 중복확인 다시 필요
                  // 메시지 초기화 (입력 변경 시)
                  setIdCheckMessage("");
                  setIdCheckStatus("idle");
                }}
                onBlur={checkDuplicateId} // 포커스 아웃 시 중복 확인
              />
              {/* 중복 확인 메시지 */}
            </div>
            {idCheckStatus !== "idle" && (
              <div
                className={
                  idCheckStatus === "available"
                    ? styles.successText
                    : idCheckStatus === "checking"
                    ? styles.infoText
                    : styles.errorText
                }
                style={{ marginTop: 6 }}
              >
                {idCheckStatus === "checking" ? "확인 중..." : idCheckMessage}
              </div>
            )}
          </div>

          <div className={styles.formGroup}>
            <label>비밀번호</label>
            <input
              type="password"
              placeholder="비밀번호를 입력하세요"
              required
              className={styles.input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" className={styles.signupButton}>
            회원가입
          </button>
        </form>
      </div>
    </div>
  );
};

export default SignupPage;
