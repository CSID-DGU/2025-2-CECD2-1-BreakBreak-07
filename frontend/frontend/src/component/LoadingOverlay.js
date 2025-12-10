import React from "react";
import styles from "./LoadingOverlay.module.css";

const LoadingOverlay = ({ message, subMessage, word }) => {
  return (
    <div className={styles.overlay}>
      <div className={styles.loader} />
      {message && <div className={styles.text}>{message}</div>}
      {subMessage && <div className={styles.sub}>{subMessage}</div>}
      {word && (
        <>
          <div className={styles.wordNotice}></div>
          <div className={styles.wordCard}>
            <div className={styles.wordTitle}>{word.word}</div>
            <div className={styles.wordMeaning}>{word.meaning}</div>
          </div>
        </>
      )}
    </div>
  );
};

export default LoadingOverlay;
