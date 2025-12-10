import React from "react";
import { useLocation } from "react-router-dom";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import "./App.css";
import Header from "./component/header";
import HeaderBeforeLogin from "./component/headerBeforeLogin";
import StartPage from "./pages/StartPage";
import LoginPage from "./pages/LoginPage";
import Diagnosis from "./pages/DiagnosisPage";
import DiagnosisVer2 from "./pages/DiagnosisVer2";
import DiagnosisEnd from "./pages/DiagnosisEnd";
import CustomStudy from "./pages/customStudy";
import OutputPage from "./pages/OutputPage";
import CreationPage from "./pages/CreationPage";
import LoginProfile from "./pages/LoginProfile";
import AddProfile from "./pages/addProfile";
import StudyMain from "./pages/studyMain";
import StudyProgress from "./pages/studyProgress";
import Quiz from "./pages/quiz";
import QuizAnswer from "./pages/quizAnswer";
import Register from "./pages/register";
import Admin from "./pages/admin";
import AdminInsert from "./pages/adminInsert";
import AdminList from "./pages/adminList";
import AdminEdit from "./pages/adminEdit";
import AdminTaleView from "./pages/adminTaleView";
import Prompting from "./pages/Prompting";
import ParentMain from "./pages/parentMain";
import ParentDetail from "./pages/parentDetail";
import ChildMyPage from "./pages/childMyPage";
import Report from "./pages/reportPage";
import RetellingPage from "./pages/RetellingPage";
import PrivateRoute from "./component/PrivateRoute";
import AdminRoute from "./component/AdminRoute";
import ReviewDetail from "./pages/reviewDetail";
import FeedbackPage from "./pages/feedbackPage";
import TaleExplain from "./pages/taleExplain";

function App() {
  return (
    <div className="app-shell">
      <div className="floating-bubbles">
        <div className="bubble planet"></div>
        <div className="bubble star"></div>
        <div className="bubble cloud"></div>
        <div className="bubble heart"></div>
        <div className="bubble leaf"></div>
      </div>
      <Router>
        <div className="app-content">
          <div className="app-frame-global">
            <AppContent />
          </div>
        </div>
      </Router>
    </div>
  );
}

function AppContent() {
  const location = useLocation();

  const showHeaderBeforeLogin = [
    "/",
    "/login",
    "/diagnosis",
    "/register",
  ].includes(location.pathname);

  return (
    <>
      {showHeaderBeforeLogin ? <HeaderBeforeLogin /> : <Header />}
      <Routes>
        {/* 로그인 필요 없음 */}
        <Route path="/" element={<StartPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<LoginPage />} />

        {/* 로그인 필요 */}
        <Route
          path="/diagnosis"
          element={
            <PrivateRoute>
              {" "}
              <Diagnosis />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/diagnosis/:childId"
          element={
            <PrivateRoute>
              {" "}
              <DiagnosisVer2 />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/diagnosisEnd"
          element={
            <PrivateRoute>
              {" "}
              <DiagnosisEnd />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/customStudy"
          element={
            <PrivateRoute>
              {" "}
              <CustomStudy />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/output"
          element={
            <PrivateRoute>
              {" "}
              <OutputPage />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/create"
          element={
            <PrivateRoute>
              {" "}
              <CreationPage />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/loginProfile"
          element={
            <PrivateRoute>
              {" "}
              <LoginProfile />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/addProfile"
          element={
            <PrivateRoute>
              {" "}
              <AddProfile />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/studyMain"
          element={
            <PrivateRoute>
              {" "}
              <StudyMain />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/study/:taleId?"
          element={
            <PrivateRoute>
              {" "}
              <StudyProgress />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/quiz"
          element={
            <PrivateRoute>
              {" "}
              <Quiz />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/quiz/answer"
          element={
            <PrivateRoute>
              {" "}
              <QuizAnswer />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/report"
          element={
            <PrivateRoute>
              {" "}
              <Report />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/retelling"
          element={
            <PrivateRoute>
              {" "}
              <RetellingPage />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <AdminRoute>
              {" "}
              <Admin />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/admin/insert"
          element={
            <AdminRoute>
              {" "}
              <AdminInsert />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/admin/list"
          element={
            <AdminRoute>
              {" "}
              <AdminList />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/admin/prompting"
          element={
            <AdminRoute>
              {" "}
              <Prompting />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/admin/edit/:taleId"
          element={
            <AdminRoute>
              {" "}
              <AdminEdit />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/admin/tale/:taleId"
          element={
            <AdminRoute>
              {" "}
              <AdminTaleView />{" "}
            </AdminRoute>
          }
        />
        <Route
          path="/parentMain"
          element={
            <PrivateRoute>
              {" "}
              <ParentMain />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/parent/:childId/detail"
          element={
            <PrivateRoute>
              {" "}
              <ParentDetail />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/mypage"
          element={
            <PrivateRoute>
              {" "}
              <ChildMyPage />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/review/:reviewId"
          element={
            <PrivateRoute>
              {" "}
              <ReviewDetail />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/feedback"
          element={
            <PrivateRoute>
              {" "}
              <FeedbackPage />{" "}
            </PrivateRoute>
          }
        />
        <Route
          path="/tale/explain"
          element={
            <PrivateRoute>
              {" "}
              <TaleExplain />{" "}
            </PrivateRoute>
          }
        />
      </Routes>
    </>
  );
}

export default App;
