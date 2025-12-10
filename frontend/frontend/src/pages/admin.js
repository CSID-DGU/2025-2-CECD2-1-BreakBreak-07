import React from "react";
import { useNavigate } from "react-router-dom";
import styles from "./admin.module.css";

function AdminMainPage() {
  const navigate = useNavigate();

  const menus = [
    { title: "동화 직접 삽입", path: "/admin/insert" },
    { title: "전체 동화 목록 조회", path: "/admin/list" },
    { title: "동화 생성하기", path: "/admin/prompting" },
  ];

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>관리자 페이지</h2>
      <div className={styles.grid}>
        {menus.map((menu, index) => (
          <button
            key={index}
            className={styles.card}
            onClick={() => navigate(menu.path)}
          >
            {menu.title}
          </button>
        ))}
      </div>
    </div>
  );
}

export default AdminMainPage;
