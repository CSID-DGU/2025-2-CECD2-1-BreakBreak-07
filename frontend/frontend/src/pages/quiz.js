import React, { useState, useEffect } from "react";
import styles from "./quiz.module.css";
import { useNavigate } from "react-router-dom";
import { getQuizAPI } from "../api/quizzes";
import { quizSubmitAPI } from "../api/quizzes";

//test

const quizData = {
  reading: [
    {
      id: 1,
      question: "What did Lily love to do the most?",
      options: [
        "Playing with her friends in the village.",
        "Dancing with fairies in the forest.",
        "Swimming in the river.",
        "Climbing oak trees.",
      ],
      answerIndex: 1,
    },
  ],
  grammar: [
    {
      id: 1,
      question: "Which sentence is grammatically correct?",
      options: [
        "She don't like apples.",
        "He go to school every day.",
        "They is happy.",
        "We are ready.",
      ],
      answerIndex: 3,
    },
  ],
  listening: [
    {
      id: 1,
      question: "What sound did Lily hear in the forest?",
      options: [
        "A river flowing.",
        "Birds singing.",
        "Fairies giggling.",
        "Leaves rustling.",
      ],
      answerIndex: 2,
    },
  ],
};

function Quiz() {
  const [quizzes, setQuizzes] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const navigate = useNavigate();

  const handleSubmit = () => {
    if (Object.keys(selectedAnswers).length !== quizzes.length) {
      alert("모든 문제에 답을 선택해주세요.");
      return;
    }

    // // 각각 문제에 대해 정답 제출 요청 보내기
    // quizzes.forEach((quiz, idx) => {
    //   const selectedIndex = selectedAnswers[idx];
    //   const questionNumber =
    //     quiz.questionNumber !== 0 ? quiz.questionNumber : idx + 1; //0이면(현재 오류) 따로 123 배정

    //   quizSubmitAPI(13, questionNumber, selectedIndex, 1); // userId는 하드코딩된 예시
    // });

    // 정답 확인 로직
    const result = quizzes.map((quiz, idx) => {
      const isCorrect = quiz.answerIndex === selectedAnswers[idx];
      return {
        isCorrect,
      };
    });

    console.log("[채점 결과]:", result);

    // 결과를 다음 페이지로 넘김
    navigate("/tale/quiz/answer", {
      state: { result },
    });
  };

  useEffect(() => {
    const getQuiz = async () => {
      try {
        const response = await getQuizAPI(13);
        console.log("[response]:", response);
        const quizList = response.data;
        console.log("[quizData]:", quizList); //quizzes에 담을 데이터
        setQuizzes(quizList);
      } catch (error) {
        console.error("Error fetching quiz: ", error);
      }
    };
    getQuiz();
  }, []);

  useEffect(() => {
    console.log("[선택한 답안]", selectedAnswers); // ✅ 추가
  }, [selectedAnswers]);

  return (
    <div className={styles.container}>
      {/* 퀴즈 번호 탭 */}
      <div className={styles.tabs}>
        {quizzes.map((quiz, i) => (
          <button
            key={i}
            className={`${styles.tab} ${
              currentIndex === i ? styles.active : ""
            }`}
            onClick={() => setCurrentIndex(i)}
          >
            {i + 1}
          </button>
        ))}
      </div>
      {quizzes.length > 0 && quizzes[currentIndex] && (
        <div className={styles.quizBox}>
          <h2>[ 문제 {currentIndex + 1} ]</h2>
          <p className={styles.questionText}>
            {quizzes[currentIndex].question}
          </p>
          <div className={styles.choices}>
            {quizzes[currentIndex].choices.map((choices, idx) => (
              <label key={idx} className={styles.optionLabel}>
                <input
                  type="radio"
                  name={`quiz-${currentIndex}`}
                  checked={selectedAnswers[currentIndex] === idx}
                  onChange={() =>
                    setSelectedAnswers((prev) => ({
                      ...prev,
                      [currentIndex]: idx,
                    }))
                  }
                />
                <span>{choices}</span>
              </label>
            ))}
          </div>
        </div>
      )}
      {/* 제출 버튼 */}
      <button className={styles.submitButton} onClick={handleSubmit}>
        정답 제출
      </button>
    </div>
  );
}

export default Quiz;
