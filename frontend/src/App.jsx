import { NavLink, Navigate, Route, Routes } from "react-router-dom";
import SubmitReport from "./SubmitReport";

export default function App() {
  return (
    <div className="layout">
      <aside className="sidebar glass">
        <h2 className="brand">Kingsforms Portal</h2>
        <NavLink to="/submit-report" className="nav-item">
          <span>📄</span> Submit-Report
        </NavLink>
        <NavLink to="/reports" className="nav-item">
          <span>📊</span> Reports
        </NavLink>
      </aside>

      <main className="content">
        <Routes>
          <Route path="/" element={<Navigate to="/submit-report" replace />} />
          <Route path="/submit-report" element={<SubmitReport />} />
          <Route path="/reports" element={<SubmitReport onlyTable />} />
        </Routes>
      </main>
    </div>
  );
}
