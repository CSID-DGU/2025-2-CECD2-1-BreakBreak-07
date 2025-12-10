import { taleInstance } from "./instance";
import { request } from "../utils/request";

// 동화 조회 요청
export const getTale = (taleId) => request(taleInstance, "get", `/${taleId}`);

//맞춤동화 베스트
export const getBestTale = (subject, tone, artStyle, ageGroup) => {
  return request(taleInstance, "post", "/options", {
    subject,
    tone,
    artStyle,
    ageGroup,
  });
};

// 기성동화 이미지 생성요청
export const oldTaleImageGen = (taleId, childId) => {
  return request(taleInstance, "post", ``, {
    taleId: taleId,
    childId: childId,
  });
};

// 기성동화 조회
export const getOldTale = () => request(taleInstance, "get", "/premade");

// 맞춤동화 생성
export const AiTaleGen = (subject, tone, artStyle, ageGroup, childId) =>
  request(taleInstance, "post", "/generate", {
    subject,
    tone,
    artStyle,
    ageGroup,
    childId,
  });

// 관리자 동화삽입 (기성동화 텍스트 삽입)
export const insertTaleAPI = (
  title,
  contents,
  subject,
  tone,
  artStyle,
  ageGroup
) => {
  return request(taleInstance, "post", "/insert", {
    title: title,
    contents: contents,
    subject: subject,
    tone: tone,
    artStyle: artStyle,
    ageGroup: ageGroup,
  });
};

// 전체 동화목록조회
export const getTaleListAPI = () => request(taleInstance, "get", "");

// 관리자 특정동화수정
export const editTaleAPI = (taleId, data) => {
  return request(taleInstance, "put", `/${taleId}`, data);
};

// 관리자 특정동화삭제
export const deleteTaleAPI = (taleId) => {
  return request(taleInstance, "delete", `/${taleId}`);
};

export const retellAPI = (taleId, childId, sceneIndex, userDescription) => {
  return request(taleInstance, "post", `/${taleId}/retell`, {
    childId: childId,
    sceneIndex: sceneIndex,
    userDescription: userDescription,
  });
};
