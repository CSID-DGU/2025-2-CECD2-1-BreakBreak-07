import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import TaleView from "../component/taleView";
import { getTale } from "../api/tale";
import { imageBaseUrl } from "../api/instance";
import styles from "./studyProgress.module.css";
import { saveUnknownWordsAPI } from "../api/child";

const StudyProgress = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const taleId = location.state?.taleId;

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [tale, setTale] = useState({
    title: "",
    contents: [],
    imageUrls: [],
  });

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // 단어 저장 배열 = 문자열 배열
  const [unknownWords, setUnknownWords] = useState([]);
  const [isSaving, setIsSaving] = useState(false);

  const handleWordSelect = (word) => {
    setUnknownWords((prev) => {
      if (prev.includes(word)) return prev;
      return [...prev, word];
    });
  };

  // FINISH 버튼 눌렀을 때 API 저장
  const handleFinish = async () => {
      if (isSaving) return;
      const child = JSON.parse(localStorage.getItem("selectedChild"));
      const childId = child?.id;

      console.log("보낼 단어 목록:", unknownWords);

      try {
          setIsSaving(true);
          const response = await saveUnknownWordsAPI(childId, unknownWords);

          const reviewWords = response.data?.responseDto || []; 
          
          console.log("저장 성공 및 상세 단어 정보:", reviewWords);
          
          // 1단계: 단어 복습 페이지로 이동 (필수)
          navigate("/tale/feedback", { 
              state: { 
                  taleId,
                  reviewWords 
              } 
          });

      } catch (err) {
          console.error("단어 저장 실패:", err);
          
          // 실패 시에도 흐름은 동일하게 단어 복습 페이지로 이동
          navigate("/tale/feedback", { 
              state: { 
                  taleId,
                  reviewWords: [] // 실패 시 빈 배열 전달
              } 
          });
      }
      finally {
          setIsSaving(false);
      }
  };

  // 동화 조회
  useEffect(() => {
    const fetchTale = async () => {
      try {
        const response = await getTale(taleId);
        const data = response.data?.responseDto || {};

        setTale({
          title: data.title || "",
          contents: data.contents || [],
          imageUrls: data.imageUrls || [],
        });

        setTotalPages(data.contents?.length || 1);
      } catch (e) {
        setError("동화를 불러오는 중 문제가 발생했어요.");
      } finally {
        setLoading(false);
      }
    };
    fetchTale();
  }, [taleId]);

  if (loading) return <div className={styles.page}>불러오는 중…</div>;
  if (error) return <div className={styles.page}>{error}</div>;

  return (
    <div className={styles.page}>
      <TaleView
        title={tale.title}
        contents={tale.contents}
        imageUrls={tale.imageUrls}
        imageSrcBuilder={(src) => `${imageBaseUrl}${src}`}
        onPageChange={(p, total) => {
          setCurrentPage(p);
          setTotalPages(total);
        }}
        onWordSelect={handleWordSelect}   // 단어 전달 추가
        onFinish={handleFinish} 
        isLastPage={currentPage === totalPages}
        isSaving={isSaving}
      />
    </div>
  );
};

export default StudyProgress;
