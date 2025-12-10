import React, { useState, useEffect } from "react";
import styles from "./adminInsert.module.css";
import { insertTaleAPI } from "../api/tale";

const optionGroups = [
  { key: "theme", label: "주제", options: ["우정", "가족", "모험", "성장"] },
  {
    key: "mood",
    label: "분위기",
    options: ["따뜻한", "신비로운", "유쾌한", "감동적인"],
  },
  {
    key: "artStyle",
    label: "그림체",
    options: ["수채화", "동양화풍", "일러스트", "손그림"],
  },
  {
    key: "ageGroup",
    label: "연령대",
    options: ["유아(3-5세)", "초등저(6-8세)", "초등고(9-11세)"],
  },
];

const AdminUploadPage = () => {
  const [title, setTitle] = useState("");
  const [contents, setContents] = useState([""]);
  const [attributes, setAttributes] = useState({
    theme: "",
    mood: "",
    artStyle: "",
    ageGroup: "",
  });

  const handleContentChange = (index, value) => {
    const updated = [...contents];
    updated[index] = value;
    setContents(updated);
  };

  const addPage = () => {
    setContents([...contents, ""]);
  };

  const handleAttributeSelect = (groupKey, option) => {
    setAttributes((prev) => ({
      ...prev,
      [groupKey]: prev[groupKey] === option ? "" : option,
    }));
  };

  const handleUpload = async () => {
    const missing = Object.entries(attributes)
      .filter(([, value]) => !value)
      .map(([key]) => key);

    if (!title.trim() || contents.some((text) => !text.trim())) {
      alert("제목과 모든 페이지 내용을 입력해주세요.");
      return;
    }

    if (missing.length > 0) {
      alert("동화 속성(주제/분위기/그림체/연령대)을 모두 선택해주세요.");
      return;
    }

    const { theme, mood, artStyle, ageGroup } = attributes;

    console.log("[속성]:", {
      subject: theme,
      tone: mood,
      artStyle: artStyle,
      ageGroup: ageGroup,
    });
    try {
      await insertTaleAPI(title, contents, theme, mood, artStyle, ageGroup);
      alert("업로드 성공!");
    } catch (err) {
      console.error(err);
      alert("업로드 실패: " + err.message);
    }
  };

  useEffect(() => {
    console.log({ title, contents });
  }, [title, contents]);

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>동화 등록</h2>

      <div className={styles.pageWrapper}>
        <div className={styles.sectionWrapper}>
          <div className={styles.section}>
            <h3 className={styles.header}>동화 정보 입력</h3>

            <h4>동화 제목</h4>
            <input
              className={styles.input}
              placeholder="동화 제목"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />

            <h4>동화 속성</h4>
            <div className={styles.optionGrid}>
              {optionGroups.map((group) => (
                <div
                  key={group.key}
                  className={
                    group.key === "ageGroup"
                      ? styles.optionColumnRow // 가로 일자 배치
                      : styles.optionColumn // 기존 세로 배치
                  }
                >
                  <p className={styles.optionLabel}>{group.label}</p>

                  <div
                    className={
                      group.key === "ageGroup"
                        ? styles.optionButtonsRow // 버튼 가로로 나열
                        : styles.optionButtons
                    }
                  >
                    {group.options.map((option) => {
                      const active = attributes[group.key] === option;
                      return (
                        <button
                          key={option}
                          type="button"
                          className={`${styles.optionButton} ${
                            active ? styles.active : ""
                          }`}
                          onClick={() =>
                            handleAttributeSelect(group.key, option)
                          }
                        >
                          {option}
                        </button>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>

            <h4>페이지별 내용</h4>
            {contents.map((text, idx) => (
              <div key={idx} className={styles.pageBlock}>
                <textarea
                  className={styles.pageTextarea}
                  placeholder={`${idx + 1} 페이지 내용`}
                  value={text}
                  onChange={(e) => handleContentChange(idx, e.target.value)}
                />
              </div>
            ))}

            <button
              type="button"
              className={styles.addPageButton}
              onClick={addPage}
            >
              + 페이지 추가
            </button>
          </div>
        </div>

        <button
          type="button"
          className={styles.uploadButton}
          onClick={handleUpload}
        >
          동화 전체 업로드
        </button>
      </div>
    </div>
  );
};

export default AdminUploadPage;
