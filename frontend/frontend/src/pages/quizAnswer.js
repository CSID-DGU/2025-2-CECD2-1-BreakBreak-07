import React, { useState, useEffect } from "react";
import styles from "./quizAnswer.module.css";
import { useNavigate, useLocation } from "react-router-dom";
import { getQuizAPI } from "../api/quizzes";
import { getQuizCorrectAPI } from "../api/quizzes";

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
      explanation:
        "장면 2에서 버디는 집 안에서 공(ball) 을 발견했어요.\n\n그 공 덕분에 즐거운 모험이 시작되었답니다!",
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
      explanation:
        "장면 4에서 버디는 공을 들고 공원(park) 으로 가서 신나게 놀았어요.\n\n정말 즐거운 하루였답니다!",
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
      why: "정답은 [Fairies giggling]입니다. \n 동화 속에서 Lily는 숲 속을 걷다가 희미하게 들려오는 요정들의 웃음소리(giggling)를 듣고 발걸음을 멈추게 됩니다.\n 이는 요정들과의 마법 같은 만남을 암시하는 중요한 단서로, 이야기의 전개에서도 핵심적인 역할을 합니다.",
    },
  ],
};

const explanationDummy = [
  "장면 2에서 Buddy는 집 안에서 공(ball) 을 발견했어요.\n그 공 덕분에 즐거운 모험이 시작되었답니다!",
  "장면 5에서 Buddy는 집으로 돌아와서 따뜻한 이불(warm blanket) 을 덮고 잠이 들었어요(fell asleep).\n 즐거운 하루를 보내고 포근하게 쉬었답니다.",
];

const QuizAnswer = () => {
  const [quizzes, setQuizzes] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const result = location.state?.result || [];

  const studyEnd = () => navigate("/studyMain");

  useEffect(() => {
    const getQuiz = async () => {
      try {
        const response = await getQuizAPI(13);
        setQuizzes(response.data);
      } catch (error) {
        console.error("Error fetching quiz: ", error);
      }
    };
    getQuiz();
    // getQuizCorrectAPI(0, 13, 1);
  }, []);

  const isCorrect = result[currentIndex]?.isCorrect;
  const explanation = result[currentIndex]?.explanation;
  const question = quizzes[currentIndex]?.question;

  return (
    <div className={styles.container}>
      {/* 문제 번호 탭 */}
      <div className={styles.tabs}>
        {quizzes.map((_, i) => (
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

      {/* 퀴즈 해설 */}
      {quizzes.length > 0 && (
        <div className={styles.quizBox}>
          <h2>{isCorrect ? "✅ 정답입니다!" : "❌ 오답입니다!"}</h2>
          <p className={styles.questionText}>Q. {question}</p>
          {/* <p className={styles.answer}>[해설] {explanation}</p> */}
          <h3>해설:</h3>
          <p className={styles.answer}>{explanationDummy[currentIndex]}</p>{" "}
        </div>
      )}

      <button className={styles.submitButton} onClick={studyEnd}>
        학습 종료
      </button>
    </div>
  );
};

export default QuizAnswer;
