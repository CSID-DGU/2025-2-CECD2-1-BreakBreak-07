import { retellingInstance } from "./instance";
import { request } from "../utils/request";

// 리텔링 제출/채점 API
// baseURL: /api/retelling
// payload 예시: { taleId, answer }
export const submitRetellingAPI = ({ taleId, answer }) => {
  return request(retellingInstance, "post", "", {
    taleId,
    answer,
  });
};
