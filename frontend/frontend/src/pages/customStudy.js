import React, {
  useMemo,
  useState,
  useEffect,
  useRef,
  useCallback,
} from "react";
import { useNavigate } from "react-router-dom";
import styles from "./customStudy.module.css";
import myAvatar from "../assets/myAvatar.png";
import { AiTaleGen, getBestTale } from "../api/tale";
import { imageBaseUrl } from "../api/instance";
import LoadingOverlay from "../component/LoadingOverlay";
import { getUnknownWordsAPI } from "../api/child";

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

const initialSelections = optionGroups.reduce(
  (acc, g) => ({ ...acc, [g.key]: "" }),
  {}
);

const CustomStoryPage = () => {
  const navigate = useNavigate();

  const [selections, setSelections] = useState(initialSelections);
  const [isGenerating, setIsGenerating] = useState(false);
  const [characterUrl, setCharacterUrl] = useState(myAvatar);
  const [selectedChild, setSelectedChild] = useState(null);
  const [loadingWord, setLoadingWord] = useState(null);
  const [bestList, setBestList] = useState([]);
  const [selectedBest, setSelectedBest] = useState(null);

  const loadingIntervalRef = useRef(null);

  const isReady = useMemo(
    () => Object.values(selections).every((v) => Boolean(v)),
    [selections]
  );

  /* 로딩 문구 */
  const [loadingWords, setLoadingWords] = useState([
    { word: "noUnknown", meaning: "모르는 단어 없음" },
    // { word: "story", meaning: "이야기" },
    // { word: "wonder", meaning: "경이" },
    // { word: "dream", meaning: "꿈" },
    // { word: "explore", meaning: "탐험하다" },
    // { word: "magic", meaning: "마법" },
  ]);

  const pickLoadingWord = useCallback(() => {
    if (!loadingWords.length) {
      setLoadingWord(null);
      return;
    }
    const idx = Math.floor(Math.random() * loadingWords.length);
    setLoadingWord(loadingWords[idx]);
  }, [loadingWords]);

  useEffect(() => {
    if (isGenerating) {
      pickLoadingWord();
      loadingIntervalRef.current = setInterval(pickLoadingWord, 4000);
    } else {
      clearInterval(loadingIntervalRef.current);
      setLoadingWord(null);
    }
  }, [isGenerating, pickLoadingWord]);

  /* 자녀 정보 불러오기 */
  useEffect(() => {
    const stored = localStorage.getItem("selectedChild");
    if (!stored) return;
    try {
      const parsed = JSON.parse(stored);
      setSelectedChild(parsed);
      if (parsed?.avatar) {
        setCharacterUrl(`${imageBaseUrl}${parsed.avatar}`);
      }
    } catch (e) {
      console.error("아이 정보 파싱 실패:", e);
    }
  }, []);

  /* 자녀 모르는 단어를 로딩 단어로 활용 */
  useEffect(() => {
    const fetchUnknownWords = async () => {
      if (!selectedChild?.id) return;

      try {
        const res = await getUnknownWordsAPI(selectedChild.id);
        const dto = res.data?.responseDto;

        let wordsSource = [];
        if (Array.isArray(dto)) {
          wordsSource = dto;
        } else if (Array.isArray(dto?.words)) {
          wordsSource = dto.words;
        }

        const normalized = wordsSource
          .map((item) => {
            const word = item.word || item.text;
            const meaning =
              item.meaningKo || item.meaning || item.kor || item.meaningEn;
            return word
              ? {
                  word,
                  meaning: meaning || "",
                }
              : null;
          })
          .filter(Boolean);

        if (normalized.length > 0) {
          setLoadingWords(normalized);
        }
      } catch (e) {
        console.error("모르는 단어 로딩 단어 조회 실패:", e);
      }
    };

    fetchUnknownWords();
  }, [selectedChild]);

  /* 옵션 선택 */
  const handleSelect = (groupKey, option) => {
    setSelections((p) => ({
      ...p,
      [groupKey]: p[groupKey] === option ? "" : option,
    }));
  };

  /* 옵션 모두 선택되면 베스트 조회 */
  useEffect(() => {
    const fetchBest = async () => {
      if (!isReady) return setBestList([]);

      try {
        const res = await getBestTale(
          selections.theme,
          selections.mood,
          selections.artStyle,
          selections.ageGroup
        );
        setBestList(res.data.responseDto || []);
      } catch (err) {
        console.error("베스트 동화 조회 실패:", err);
        setBestList([]);
      }
    };

    fetchBest();
  }, [isReady, selections]);

  /* 생성 */
  const handleGenerate = async () => {
    if (!isReady) return alert("모든 옵션을 선택해주세요!");
    if (!selectedChild?.id) return alert("학생 정보 오류입니다.");

    setIsGenerating(true);

    try {
      const { theme, mood, artStyle, ageGroup } = selections;
      const res = await AiTaleGen(
        theme,
        mood,
        artStyle,
        ageGroup,
        selectedChild.id
      );

      console.log("생성 API 응답:", res.data.responseDto);
      const newTaleId = res.data?.responseDto?.taleId;

      if (newTaleId) {
        navigate("/tale/explain", {
          state: {
            taleId: newTaleId,
            story: res.data.responseDto.story,
            selections,
            reason: res.data.responseDto.reason, // ← API가 reason(단수)인지 reasons인지 확인
          },
        });
      }
    } catch (e) {
      alert("동화 생성 중 오류가 발생했습니다.");
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.pageHeader}>
        <p className={styles.headerEyebrow}>AI 생성 동화</p>
        <p className={styles.headerTitle}>나만의 맞춤 동화 만들기</p>
      </div>

      {/* 아바타 + 옵션 */}
      <div className={styles.optionWithAvatar}>
        <div className={styles.avatarCard}>
          <img src={characterUrl} className={styles.avatarImg} />
          <p className={styles.avatarText}>이 아바타로 동화가 생성돼요</p>
        </div>

        <section className={styles.optionsCard}>
          <h3>옵션 선택</h3>
          <div className={styles.optionGrid}>
            {optionGroups.map((group) => (
              <div key={group.key} className={styles.optionColumn}>
                <p className={styles.optionLabel}>{group.label}</p>

                <div className={styles.optionButtons}>
                  {group.options.map((option) => {
                    const active = selections[group.key] === option;
                    return (
                      <button
                        key={option}
                        className={`${styles.optionButton} ${
                          active ? styles.active : ""
                        }`}
                        onClick={() => handleSelect(group.key, option)}
                      >
                        {option}
                      </button>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>

      {/* 베스트 생성동화 */}
      <section className={styles.resultsWrapper}>
        <div className={styles.resultBox}>
          {!isReady ? (
            <>
              <p className={styles.resultText}>모든 옵션을 선택해주세요</p>
              <p className={styles.resultTextSmall}>
                옵션을 모두 선택하면 베스트 동화를 보여드릴게요
              </p>
            </>
          ) : (
            <>
              <p className={styles.bestSectionTitle}>
                선택한 옵션의 베스트 생성동화
              </p>

              {bestList.length === 0 ? (
                <div className={styles.noBestBox}>
                  <p className={styles.noBestTextSmall}>
                    해당 옵션에 베스트동화가 없어요{" "}
                  </p>

                  <button
                    className={styles.generateInlineButton}
                    onClick={handleGenerate}
                  >
                    새로
                    <br />
                    생성하기
                  </button>
                </div>
              ) : (
                <div className={styles.bestRow}>
                  {bestList.map((item) => (
                    <div
                      key={item.id}
                      className={styles.bestCardMini}
                      onClick={() => setSelectedBest(item)}
                    >
                      <img
                        src={`${process.env.REACT_APP_URL}${item.thumbnail}`}
                        className={styles.bestThumbMini}
                      />
                      <p className={styles.bestNameMini}>{item.title}</p>
                    </div>
                  ))}

                  <button
                    className={styles.generateInlineButton}
                    onClick={handleGenerate}
                  >
                    새로
                    <br />
                    생성하기
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>

      {/* 로딩 오버레이 */}
      {isGenerating && (
        <LoadingOverlay
          light
          message="동화를 생성하는 중이에요…"
          subMessage="약 1분 정도 걸려요. 로딩되는 동안 모른다고 눌렀던 단어를 다시 학습해봅시다."
          word={loadingWord}
        />
      )}

      {/* 베스트 선택 오버레이 */}
      {selectedBest && (
        <div
          className={styles.bestOverlay}
          onClick={() => setSelectedBest(null)}
        >
          <div
            className={styles.bestOverlayCard}
            onClick={(e) => e.stopPropagation()}
          >
            <p className={styles.bestOverlayEyebrow}>선택한 생성 동화</p>
            <h3 className={styles.bestOverlayTitle}>{selectedBest.title}</h3>

            <div className={styles.bestOverlayThumbWrap}>
              <img
                src={`${process.env.REACT_APP_URL}${selectedBest.thumbnail}`}
                className={styles.bestOverlayThumb}
              />
            </div>

            <p className={styles.bestOverlayHint}>
              이 동화로 바로 학습을 시작할까요?
            </p>

            <div className={styles.bestOverlayButtons}>
              <button
                className={styles.bestOverlayCancel}
                onClick={() => setSelectedBest(null)}
              >
                취소
              </button>
              <button
                className={styles.bestOverlayConfirm}
                onClick={() =>
                  navigate("/tale/study", {
                    state: { taleId: selectedBest.id },
                  })
                }
              >
                이 동화로 학습하기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomStoryPage;
