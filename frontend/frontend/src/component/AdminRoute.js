import React from "react";
import { Navigate } from "react-router-dom";

function AdminRoute({ children }) {
  const user = JSON.parse(localStorage.getItem("user") || "{}");

  if (!user || user.role !== "ADMIN") {
    alert("접근 권한이 없습니다.");
    return <Navigate to="/" replace />;
  }

  return children;
}

export default AdminRoute;
