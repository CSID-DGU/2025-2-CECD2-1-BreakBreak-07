import { reportInstance } from "./instance";
import { request } from "../utils/request";

// 독후감 제출 API (감정 + 별점만 전송)
export const submitReportAPI = ({ childId, taleId, rating, feeling }) => {
  return request(reportInstance, "post", `/child/${childId}/tales/${taleId}`, {
    rating,
    feeling: Array.isArray(feeling) ? feeling.join(",") : feeling,
  });
};
