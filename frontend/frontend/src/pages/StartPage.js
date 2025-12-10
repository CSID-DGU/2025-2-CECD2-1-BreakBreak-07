import React, { useEffect, useRef, useState } from "react";
import styles from "./StartPage.module.css";
import owlImage from "../assets/owl_girl.png";
import { useNavigate } from "react-router-dom";

export default function StartPage() {
  const navigate = useNavigate();
  const [ctaState, setCtaState] = useState("idle"); // idle | loading | success
  const timerRef = useRef([]);

  const goToLogin = () => {
    navigate("/login");
  };

  const handleClick = () => {
    if (ctaState !== "idle") return;
    setCtaState("loading");

    const successTimer = setTimeout(() => {
      setCtaState("success");
    }, 360);
    const navigateTimer = setTimeout(() => {
      goToLogin();
    }, 1800);

    timerRef.current = [successTimer, navigateTimer];
  };

  useEffect(() => {
    return () => {
      timerRef.current.forEach(clearTimeout);
    };
  }, []);

  return (
    <>
      <div className={styles.homecontainer}>
        <div className={styles.left}>
          <img src={owlImage} alt="Girl with owl" />
        </div>
        <div className={styles.right}>
          <h1>
            나만의 AI 동화책을
            <br /> 만나보세요!
          </h1>
          <p>
            당신의 취향에 딱 맞춘 맞춤형 영어 동화를
            <br />
            AI가 직접 만들어드려요.
          </p>
          <button
            className={`${styles.btn} ${styles.progressButton} ${
              ctaState === "loading" ? styles.isLoading : ""
            } ${ctaState === "success" ? styles.isSuccess : ""}`}
            onClick={handleClick}
            aria-disabled={ctaState !== "idle"}
          >
            <span className={styles.progressLabel}>
              {ctaState === "success"
                ? "준비 완료!"
                : ctaState === "loading"
                ? "시작 준비 중..."
                : "지금 시작하기"}
            </span>
            <span className={styles.progressBar}></span>
            <svg
              className={styles.checkIcon}
              viewBox="0 0 25 30"
              aria-hidden="true"
              focusable="false"
            >
              <path
                className={styles.checkPath}
                d="M2,19.2C5.9,23.6,9.4,28,9.4,28L23,2"
              />
            </svg>
          </button>
        </div>
      </div>
    </>
  );
}
