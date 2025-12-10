import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./childMyPage.module.css";
import owlGirl from "../assets/owl_girl.png";
import defaultCover from "../assets/fairy.png";
import creditIcon from "../assets/credit.png";
import { getChildMyPage, updateChildInfo } from "../api/mypage";
import { getChildReviews } from "../api/review";
import { imageBaseUrl } from "../api/instance"; 

const ChildMyPage = () => {
  const navigate = useNavigate();

  const [childData, setChildData] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editableFields, setEditableFields] = useState({
    name: "",
    birthdate: "",
  });

  const [isReportListOpen, setIsReportListOpen] = useState(false);
  const [reportList, setReportList] = useState([]);
  const [isReportLoading, setIsReportLoading] = useState(false);

  const formatDate = (dateString) => {
    if (!dateString) return "";
    const d = new Date(dateString);
    if (isNaN(d.getTime())) return "";
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  // 💡 생년월일(YYYY-MM-DD)을 만 나이로 계산하는 함수 추가
  const calculateAge = (birthdate) => {
    const birth = new Date(birthdate);
    if (isNaN(birth.getTime())) return null;

    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();

    // 생일이 지나지 않았으면 나이에서 1을 뺌 (만 나이 기준)
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
  };   

  const goSwitchChild = () => navigate("/loginProfile");

  useEffect(() => {
    const selectedChild = JSON.parse(localStorage.getItem("selectedChild"));

    if (!selectedChild) {
      alert("프로필을 먼저 선택해주세요.");
      navigate("/loginProfile");
      return;
    }

    const childId = selectedChild.id;

    const loadChildMyPage = async () => {
      try {
            const dto = await getChildMyPage(childId); 

            setChildData({
              child: dto.child, 
              recentTale: dto.recentTale || null, // recentTale을 dto에서 직접 추출
              reportSummary: dto.reportSummary,
            });

            const list = await getChildReviews(childId);
            setReportList(list);

      } catch (err) {
        console.error("마이페이지 불러오기 실패:", err);
        alert("자녀 정보를 불러오지 못했습니다.");
      }
    };

    loadChildMyPage();
  }, [navigate]);


  useEffect(() => {
    if (!childData?.child) return;
    console.log("childData:", childData);


    setEditableFields({
      name: childData.child.name || "",
      birthdate: formatDate(childData.child.birthdate) || "",
    });
  }, [childData]);

  if (!childData || !childData.child) {
    return (
      <div className={styles.page}>
        <div className={styles.empty}>
          <p>자녀 정보를 불러오는 중입니다...</p>
        </div>
        <button className={styles.switchChildBtn} onClick={goSwitchChild}>
          자녀 전환
        </button>
      </div>
    );
  }

  const { child, recentTale, reportSummary } = childData;

  const name = child?.name ?? "";
  const avatar = child?.characterImageUrl
    ? child.characterImageUrl.startsWith("http")
      ? child.characterImageUrl
      : `${imageBaseUrl}${child.characterImageUrl}` 
    : owlGirl;

  const creditBalance = child?.credit ?? 0;
  const interests = [
    child.preferSubject,
    child.preferTone,
    child.preferStyle,
    child.preferAge,
  ].filter(Boolean);  
  const recentBookTitle = recentTale?.title ?? "기록 없음";

  const recentBookCover = recentTale?.thumbnail
    ? `${imageBaseUrl}${recentTale.thumbnail}`
    : defaultCover;

  //const reportCount = reportSummary?.totalCount ?? 0;
  const reportCount = reportList.length;


  const openEditModal = () => setIsEditing(true);
  const closeEditModal = () => setIsEditing(false);

  const handleFieldChange = (field, value) => {
    setEditableFields((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => { // async 키워드 유지
    const trimmedName = editableFields.name.trim();
    const birthdate = editableFields.birthdate;
    
    if (!trimmedName) {
      alert("이름을 입력해 주세요.");
      return;
    }
    
    if (!birthdate) {
      alert("생년월일을 입력해 주세요.");
      return;
    }

    const childId = child.id; 
    const ageToSend = calculateAge(birthdate); // 생년월일 -> 만 나이 계산

    if (ageToSend === null || ageToSend < 0) {
      alert("유효하지 않은 생년월일입니다.");
      return;
    }

    try {
      // 1. 서버에 이름과 계산된 나이 전송
      const updatePayload = {
        childName: trimmedName, // 💡 **수정 완료: name 대신 childName 사용**
        age: ageToSend,         // 계산된 Age를 서버로 전송
      };
      
      // PUT api/user/child/{childId} API 호출
      await updateChildInfo(childId, updatePayload); 

      // 2. 서버 업데이트 성공 시 로컬 상태 및 LocalStorage 업데이트
      //    (마이페이지 UI에 즉시 반영)
      const updatedChild = {
        ...child,
        name: trimmedName,
        birthdate: birthdate, 
      };
      setChildData((prev) => ({ ...prev, child: updatedChild }));
      
      // 3. 프로필 선택 화면 등에 변경된 이름이 반영되도록 LocalStorage 업데이트
      const selectedChild = JSON.parse(localStorage.getItem("selectedChild"));
      if (selectedChild && selectedChild.id === childId) {
        const updatedSelectedChild = { ...selectedChild, name: trimmedName };
        localStorage.setItem("selectedChild", JSON.stringify(updatedSelectedChild));
      }

      setIsEditing(false);

    } catch (err) {
      console.error("정보 수정 실패:", err);
      alert("정보 수정에 실패했습니다. 다시 시도해 주세요.");
    }
  };

  const openReportList = async () => {
    setIsReportListOpen(true);
    setIsReportLoading(true);

    const selectedChild = JSON.parse(localStorage.getItem("selectedChild"));
    const childId = selectedChild.id;

    try {
      const list = await getChildReviews(childId);
      setReportList(list);
    } catch (err) {
      console.error("리포트 조회 실패:", err);
      setReportList([]);
    } finally {
      setIsReportLoading(false);
    }
  };

  const closeReportList = () => setIsReportListOpen(false);

  const handleAvatarClick = () => {
    const ok = window.confirm("아바타를 수정하시겠습니까?");
    if (ok)
      navigate(`/diagnosis/${child.id}`, {
        state: {
          editMode: true,
          selectedItems: {
            hair: child.hair,
            clothes: child.clothes,
            shoes: child.shoes,
            accessory: child.accessory,
          },
        },
      });
  };

  return (
    <div className={styles.page}>
      {/* --- 프로필 섹션 --- */}
      <section className={styles.profileSection}>
        <img
          src={avatar}
          alt="avatar"
          className={styles.profileImage}
          onClick={handleAvatarClick}
          style={{ cursor: "pointer" }}
        />

        <div className={styles.profileContent}>
          <div className={styles.profileTopRow}>
            <h2 className={styles.greeting}>{name}님, 반가워요!</h2>
            <div className={styles.creditBadge}>
              <img
                src={creditIcon}
                alt="크레딧 아이콘"
                className={styles.creditIcon}
              />
              <div className={styles.creditText}>
                <strong className={styles.creditValue}>
                  {creditBalance.toLocaleString()}C
                </strong>
              </div>
            </div>
          </div>

          <div className={styles.tagContainer}>
            {interests.length > 0 ? (
              interests.map((interest) => (
                <span key={interest} className={styles.tag}>
                  #{interest}
                </span>
              ))
            ) : (
              <span className={styles.noTags}>관심 주제가 아직 없어요</span>
            )}
          </div>
        </div>

        <div className={styles.profileAside}>
          <button
            type="button"
            onClick={openEditModal}
            className={styles.editBtnRight}
          >
            정보 수정
          </button>
          <button
            type="button"
            onClick={goSwitchChild}
            className={styles.switchChildBtn}
          >
            자녀 전환
          </button>
        </div>
      </section>
 
      {/* --- 최근 읽은 책 / 리포트 요약 --- */}
      <section className={styles.summarySection}>
        <div className={styles.card}>
          <h3>최근 읽은 책</h3>

          <div className={styles.book}>
            <img src={recentBookCover} alt="최근 책 표지" />

            <div className={styles.bookMeta}>
              <strong>{recentBookTitle}</strong>
            </div>
          </div>
        </div>

        <div className={styles.card}>
          <h3>리포트 현황</h3>
          <p>
            총 작성 리포트 수: <strong>{reportCount}개</strong>
          </p>
          <button className={styles.primaryBtn} onClick={openReportList}>
            작성한 리포트 보기
          </button>
        </div>
      </section>

      {/* 정보 수정 팝업 */}
      {isEditing && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalPanel}>
            <div className={styles.modalHeader}>
              <h2>내 정보 수정</h2>
              <button className={styles.closeBtn} onClick={closeEditModal}>
                ✕
              </button>
            </div>

            <div className={styles.modalBody}>
              <label className={styles.modalField}>
                <span>이름</span>
                <input
                  type="text"
                  value={editableFields.name}
                  onChange={(e) => handleFieldChange("name", e.target.value)}
                />
              </label>

              <label className={styles.modalField}>
                <span>생년월일</span>
                <input
                  type="date"
                  value={editableFields.birthdate}
                  onChange={(e) =>
                    handleFieldChange("birthdate", e.target.value)
                  }
                />
              </label>
            </div>

            <div className={styles.modalActions}>
              <button className={styles.primaryBtn} onClick={handleSave}>
                저장하기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* --- 리포트 팝업 --- */}
      {isReportListOpen && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalPanel}>
            <div className={styles.modalHeader}>
              <h2>작성한 독후감 ({reportCount}개)</h2>
              <button className={styles.closeBtn} onClick={closeReportList}>
                ✕
              </button>
            </div>

            <div className={styles.modalBody}>
              {isReportLoading ? (
                <p>로딩중...</p>
              ) : reportList.length === 0 ? (
                <div className={styles.reportEmpty}>
                  <p>아직 작성한 독후감이 없어요.</p>
                </div>
              ) : (
                <ul className={styles.reportList}>
                  {reportList.map((r) => (
                    <li
                      key={r.reviewId}
                      className={styles.reportItem}
                      onClick={() =>
                        navigate(`/review/${r.reviewId}?from=mypage`)
                      } // 상세페이지 이동 추가
                      style={{ cursor: "pointer" }}
                    >
                      <strong className={styles.reportItemTitle}>
                        {r.title || "제목 없음"}
                      </strong>

                      <p className={styles.reportSummary}>
                        {r.memorableScene || "내용 없음"}
                      </p>

                      <span className={styles.reportDate}>
                        {(r.updatedAt || r.createdAt).split("T")[0]}
                      </span>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChildMyPage;
