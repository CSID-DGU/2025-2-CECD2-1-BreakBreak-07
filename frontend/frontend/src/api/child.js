import { defaultInstance } from "./instance";

export const saveUnknownWordsAPI = (childId, words) => {
  return defaultInstance.post(`/child/${childId}/words`, words); 
};

export const getUnknownWordsAPI = (childId) => {
    return defaultInstance.get(`/child/${childId}/words`);
};