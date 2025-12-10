import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TaleView from "../component/taleView";
import { getTale } from "../api/tale";
import { imageBaseUrl } from "../api/instance";
import styles from "./adminTaleView.module.css";

function AdminTaleView() {
  const { taleId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [tale, setTale] = useState({
    title: "",
    contents: [],
    imageUrls: [],
  });

  useEffect(() => {
    let mounted = true;
    const fetchTale = async () => {
      setLoading(true);
      setError("");
      try {
        const taleRes = await getTale(taleId);
        const taleData = taleRes.data?.responseDto || {};
        if (!mounted) return;
        setTale({
          title: taleData.title || "",
          contents: Array.isArray(taleData.contents) ? taleData.contents : [],
          imageUrls: Array.isArray(taleData.imageUrls)
            ? taleData.imageUrls
            : [],
        });
      } catch (err) {
        console.error("동화 조회 실패:", err);
        if (!mounted) return;
        setError("동화를 불러오는 중 문제가 발생했어요.");
      } finally {
        if (mounted) setLoading(false);
      }
    };

    fetchTale();
    return () => {
      mounted = false;
    };
  }, [taleId]);

  const handleBack = () => {
    navigate("/admin/list");
  };

  if (loading) {
    return (
      <div className={styles.page}>
        <div className={styles.stateMessage}>동화를 불러오는 중입니다…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.page}>
        <div className={`${styles.stateMessage} ${styles.error}`}>{error}</div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <TaleView
        title={tale.title}
        contents={tale.contents}
        imageUrls={tale.imageUrls}
        variant="admin"
        imageSrcBuilder={(src) => (src ? `${imageBaseUrl}${src}` : src)}
      />
    </div>
  );
}

export default AdminTaleView;
