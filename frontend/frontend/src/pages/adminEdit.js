import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./adminEdit.module.css";
import { getTale, editTaleAPI } from "../api/tale";

function EditTalePage() {
  const { taleId } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [contents, setContents] = useState([]);
  const [imageUrls, setImageUrls] = useState([]);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await getTale(taleId);
        const data = res.data?.responseDto;

        setTitle(data.title || "");
        setContents(data.contents || []);
        setImageUrls(data.imageUrls || []);
      } catch (err) {
        console.error("불러오기 실패:", err);
      }
    };
    fetchData();
  }, [taleId]);

  const handleSave = async () => {
    if (!title.trim()) {
      setMessage("제목을 입력해주세요.");
      return;
    }

    setSaving(true);
    setMessage("");

    try {
      await editTaleAPI(taleId, { title, contents, imageUrls });
      alert("수정 완료!");
      navigate(`/admin/tale/${taleId}`);
    } catch (err) {
      console.error("수정 실패:", err);
      alert("오류가 발생했습니다.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>동화 수정</h2>
      <div className={styles.pageWrapper}>
        <div className={styles.sectionWrapper}>
          <div className={styles.section}>
            <h4 className={styles.header}>제목</h4>
            <input
              className={styles.input}
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />

            <h4 className={styles.header}>내용</h4>
            {contents.map((content, idx) => (
              <textarea
                key={idx}
                className={styles.pageTextarea}
                value={content}
                onChange={(e) => {
                  const updated = [...contents];
                  updated[idx] = e.target.value;
                  setContents(updated);
                }}
              />
            ))}

            <h4 className={styles.header}>이미지 URL</h4>
            {imageUrls.map((url, idx) => (
              <input
                key={idx}
                className={styles.input}
                value={url}
                onChange={(e) => {
                  const updated = [...imageUrls];
                  updated[idx] = e.target.value;
                  setImageUrls(updated);
                }}
              />
            ))}

            <button
              className={styles.uploadButton}
              disabled={saving}
              onClick={handleSave}
            >
              {saving ? "저장 중..." : "수정 완료"}
            </button>

            {message && <p className={styles.message}>{message}</p>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditTalePage;
