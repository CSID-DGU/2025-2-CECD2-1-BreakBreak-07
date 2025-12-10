import { quizzesInstance } from "./instance";
import { request } from "../utils/request";

// 퀴즈 GET 요청
export const getQuizAPI = (taleId) =>
  request(quizzesInstance, "get", `/${taleId}`);

// 퀴즈 정답 제출
export const quizSubmitAPI = (taleId, questionNumber, selectedIndex, userId) =>
  request(quizzesInstance, "post", `/submit`, {
    taleId: taleId,
    questionNumber: questionNumber,
    selectedIndex: selectedIndex,
    userId: userId,
  });

// 퀴즈 정답 조회
export const getQuizCorrectAPI = (userId, taleId, questionNumber) =>
  request(
    quizzesInstance,
    "get",
    `/${userId}/${taleId}/${questionNumber}/answer`
  );
