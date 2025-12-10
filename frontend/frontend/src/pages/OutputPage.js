import React from 'react';
import { useLocation } from 'react-router-dom';
import { useNavigate } from "react-router-dom";
import styles from './OutputPage.module.css';

// 더미 이미지 데이터
const DUMMY_IMAGES = [
  'https://via.placeholder.com/300?text=Image+1',
  'https://via.placeholder.com/300?text=Image+2',
  'https://via.placeholder.com/300?text=Image+3',
  'https://via.placeholder.com/300?text=Image+4',
  'https://via.placeholder.com/300?text=Image+5',
];

function OutputPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { title, storyText } = location.state || { title: '나만의 동화', storyText: '멋진 동화 이야기' };

  return (
    <div className={styles.page}>
      <h2 className={styles.header}>나만의 동화 만들기</h2>
      <div className={styles.row}>
        <section className={styles.outputCard}>
          <h3 className={styles.cardTitle}>이미지 출력 미리보기</h3>
          <div className={styles.imageGrid}>
            {DUMMY_IMAGES.map((imgUrl, index) => (
              <img
                key={index}
                src={imgUrl}
                alt={`생성된 동화 이미지 ${index + 1}`}
                className={styles.storyImage}
              />
            ))}
          </div>
          {/*
        </section>
      </div>*/}
      
      
      <div className={styles.cardFooter}>
          <button
            type="button"
            className={styles.ghostBtn}
            onClick={() => navigate("/prompting")}
          >
            돌아가기
          </button>
          <button
            type="button"
            className={styles.primaryBtn}
            onClick={() => navigate("/create")}
          >
            동화 만들기
          </button>
        </div>
      </section>
    </div>

    </div>
    
  );
}

export default OutputPage;