import { reportInstance } from "./instance";
import { request } from "../utils/request";

export const getReviewDetailAPI = async (reviewId) => {
  const res = await request(reportInstance, "get", `/${reviewId}`);
  console.log("상세조회 응답:", res);    
  
  return res?.data?.responseDto ?? res?.responseDto ?? res;
};

export const getChildReviews = async (childId) => {
  const res = await request(reportInstance, "get", `/child/${childId}`);
  return res.data?.responseDto ?? res.responseDto ?? [];
}; 
