import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./DiagnosisPage.module.css"; // ✅ 수정된 import

import animalIcon from "../assets/animal.png";
import magicIcon from "../assets/magic.png";
import adventureIcon from "../assets/adventure.png";

import lora1 from "../assets/loraA.png";
import lora2 from "../assets/loraB.png";
import lora3 from "../assets/loraC.png";

export default function DiagnosisPage() {
  const [step, setStep] = useState(0);
  const [answers, setAnswers] = useState({ topic: "", style: "", age: "" });

  const handleAnswer = (field, value) => {
    setAnswers((prev) => ({ ...prev, [field]: value }));
    const answeredCount = Object.values({ ...answers, [field]: value }).filter(
      Boolean
    ).length;
    setStep(answeredCount);
  };

  const navigate = useNavigate();

  const handleSubmit = () => {
    console.log("handleSubmit 실행됨");
    if (step === 3) {
      console.log("navigate 실행됨");
      navigate("/login");
    }
  };

  const imageMap = {
    동물: animalIcon,
    마법: magicIcon,
    모험: adventureIcon,
  };

  const loraImg = [lora1, lora2, lora3];

  return (
    <div className={styles.diagnosisPage}>
      <div className={styles.progressContainer}>
        <div className={styles.progressBar}>
          <div
            className={styles.progress}
            style={{ width: `${(step / 3) * 100}%` }}
          />
        </div>
        <span className={styles.progressText}>{step} / 3</span>
      </div>

      <div className={styles.questionSection}>
        <p className={styles.questionTitle}>좋아하는 주제는 무엇인가요?</p>
        <div className={styles.choiceButtons}>
          {["동물", "마법", "모험"].map((choice) => (
            <div key={choice} className={styles.choiceWrapper}>
              <button
                className={`${styles.choiceBox} ${
                  answers.topic === choice ? styles.selected : ""
                }`}
                onClick={() => handleAnswer("topic", choice)}
              >
                <img
                  src={imageMap[choice]}
                  alt={choice}
                  className={styles.choiceIcon}
                />
              </button>
              <span className={styles.choiceLabel}>{choice}</span>
            </div>
          ))}
        </div>
      </div>

      <div className={styles.questionSection}>
        <p className={styles.questionTitle}>선호하는 캐릭터는 어떤 것인가요?</p>
        <div className={styles.choiceButtons}>
          {loraImg.map((img, index) => (
            <div key={index}>
              <button
                className={`${styles.loraButton} ${
                  answers.style === img ? styles.selected : ""
                }`}
                onClick={() => handleAnswer("style", img)}
              >
                <img
                  src={img}
                  className={styles.loraImg}
                  alt={`lora${index + 1}`}
                />
              </button>
              <span className={styles.imgLabel}>{`${index + 1}번`}</span>
            </div>
          ))}
        </div>
      </div>

      <div className={styles.questionSection}>
        <p className={styles.questionTitle}>연령대를 선택해 주세요.</p>
        <select
          className={styles.ageSelect}
          value={answers.age}
          onChange={(e) => handleAnswer("age", e.target.value)}
        >
          <option value="">연령대 선택</option>
          {["5세", "6세", "7세", "8세", "9세", "10세 이상"].map((age) => (
            <option key={age} value={age}>
              {age}
            </option>
          ))}
        </select>
      </div>

      <button
        className={styles.submitButton}
        disabled={step < 3}
        onClick={handleSubmit}
      >
        완료
      </button>
    </div>
  );
}
