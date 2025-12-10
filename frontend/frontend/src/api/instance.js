import axios from "axios";

const BASE_URL = process.env.REACT_APP_URL;

const defaultInstance = axios.create({
  baseURL: `${BASE_URL}/api`,
});

// user 인스턴스
const userInstance = axios.create(defaultInstance.defaults);
userInstance.defaults.baseURL += "/user";

// tale 인스턴스
const taleInstance = axios.create(defaultInstance.defaults);
taleInstance.defaults.baseURL += "/tales";

const quizzesInstance = axios.create(defaultInstance.defaults);
quizzesInstance.defaults.baseURL += "/quizzes";

// gemini 이미지생성 인스턴스
const promptInstance = axios.create(defaultInstance.defaults);
promptInstance.defaults.baseURL += "/gemini";

// 리텔링(장면 다시 쓰기) 인스턴스 - 향후 별도 엔티티/컨트롤러용
const retellingInstance = axios.create(defaultInstance.defaults);
retellingInstance.defaults.baseURL += "/retelling";

// 서버 저장된 이미지 접근용 인스턴스
const imageBaseUrl = `${BASE_URL}`;

// 독후감 인스턴스
const reportInstance = axios.create(defaultInstance.defaults);
reportInstance.defaults.baseURL += "/review";

//인증 토큰 붙이기
const attachToken = (instance) => {
  instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );
};

//토큰이 필요한 인스턴스
attachToken(defaultInstance);
attachToken(userInstance);
attachToken(taleInstance);
attachToken(reportInstance);
attachToken(retellingInstance);

export {
  defaultInstance,
  taleInstance,
  userInstance,
  quizzesInstance,
  promptInstance,
  retellingInstance,
  imageBaseUrl,
  reportInstance,
};
