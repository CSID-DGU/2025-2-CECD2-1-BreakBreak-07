import React from "react";
import { Navigate } from "react-router-dom";

function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  console.log("PrivateRoute token:", localStorage.getItem("token"));
  console.log("PrivateRoute pathname:", window.location.pathname);


  if (!token) {
    alert("로그인이 필요합니다.");
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default PrivateRoute;
