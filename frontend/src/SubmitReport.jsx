import { useEffect, useMemo, useState } from "react";
import { downloadExport, downloadTemplate, fetchReports, submitReport, uploadReportFile } from "./reportsApi";

const attendanceOptions = ["Yes", "No", "Officially Excused"];

const initialForm = {
  zoneName: "",
  zonalManager: "",
  totalPartnershipRemittance: "",
  newPartnersRecruited: "",
  testimoniesSubmitted: "",
  httnmTranslations: "",
  httnmOutreaches: "",
  outreachMediaSubmitted: "",
  zonalPastorAttendance: "",
  zonalManagerDirectorMeetingAttendance: "",
  zonalManagerStrategyMeetingAttendance: "",
  testimonyClarificationConcern: "",
  submittedByEmail: "",
  participationPrayWithMe: "",
  totalRegistrationHslhs: "",
  heraldConference: ""
};

export default function SubmitReport({ onlyTable = false }) {
  const [form, setForm] = useState(initialForm);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const requiredKeys = useMemo(
    () => [
      "zoneName",
      "zonalManager",
      "totalPartnershipRemittance",
      "newPartnersRecruited",
      "testimoniesSubmitted",
      "httnmTranslations",
      "httnmOutreaches",
      "outreachMediaSubmitted",
      "zonalPastorAttendance",
      "zonalManagerDirectorMeetingAttendance",
      "zonalManagerStrategyMeetingAttendance",
      "submittedByEmail",
      "participationPrayWithMe",
      "totalRegistrationHslhs",
      "heraldConference"
    ],
    []
  );

  const progress = Math.round(
    (requiredKeys.filter((k) => String(form[k] ?? "").trim()).length / requiredKeys.length) * 100
  );

  useEffect(() => {
    loadReports();
  }, []);

  async function loadReports(email = form.submittedByEmail) {
    const data = await fetchReports(email || undefined);
    setReports(data);
  }

  function onInputChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setMessage("");
    const hasMissing = requiredKeys.some((k) => !String(form[k] ?? "").trim());
    if (hasMissing) return setMessage("Please fill all required fields.");

    setLoading(true);
    try {
      await submitReport({
        ...form,
        totalPartnershipRemittance: Number(form.totalPartnershipRemittance),
        newPartnersRecruited: Number(form.newPartnersRecruited),
        testimoniesSubmitted: Number(form.testimoniesSubmitted),
        httnmTranslations: Number(form.httnmTranslations),
        httnmOutreaches: Number(form.httnmOutreaches),
        outreachMediaSubmitted: Number(form.outreachMediaSubmitted),
        totalRegistrationHslhs: Number(form.totalRegistrationHslhs)
      });
      await loadReports(form.submittedByEmail);
      setMessage("Report submitted and saved to DB.");
      setForm((prev) => ({ ...initialForm, submittedByEmail: prev.submittedByEmail }));
    } catch (err) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function onUpload(e) {
    const file = e.target.files?.[0];
    if (!file) return;
    try {
      await uploadReportFile(file);
      await loadReports();
      setMessage("File uploaded and reports imported.");
    } catch (err) {
      setMessage(err.message);
    }
  }

  return (
    <div className="hero-bg">
      <div className="glass card fade-in">
        <h1>✨ Submit Report</h1>
        <p>Kingsforms October Week 1 report</p>
        {!onlyTable && (
          <>
            <div className="progress-wrap">
              <div className="progress-label">Progress: {progress}% Complete</div>
              <div className="progress-track"><div className="progress-fill" style={{ width: `${progress}%` }} /></div>
            </div>
            <form className="form-grid" onSubmit={onSubmit}>
              <label>Name of Zone *<input name="zoneName" value={form.zoneName} onChange={onInputChange} /></label>
              <label>Zonal Manager *<input name="zonalManager" value={form.zonalManager} onChange={onInputChange} /></label>
              <label>Total Partnership Remittance *<input type="number" name="totalPartnershipRemittance" value={form.totalPartnershipRemittance} onChange={onInputChange} /></label>
              <label>New Partners Recruited *<input type="number" name="newPartnersRecruited" value={form.newPartnersRecruited} onChange={onInputChange} /></label>
              <label>Testimonies Submitted *<input type="number" name="testimoniesSubmitted" value={form.testimoniesSubmitted} onChange={onInputChange} /></label>
              <label>HTTNM Translations *<input type="number" name="httnmTranslations" value={form.httnmTranslations} onChange={onInputChange} /></label>
              <label>HTTNM Outreaches *<input type="number" name="httnmOutreaches" value={form.httnmOutreaches} onChange={onInputChange} /></label>
              <label>Pictures/Videos Submitted *<input type="number" name="outreachMediaSubmitted" value={form.outreachMediaSubmitted} onChange={onInputChange} /></label>
              <label>Zonal Pastor Attendance *
                <select name="zonalPastorAttendance" value={form.zonalPastorAttendance} onChange={onInputChange}>
                  <option value="">Please select</option>
                  {attendanceOptions.map((o) => <option key={o} value={o}>{o}</option>)}
                </select>
              </label>
              <label>Zonal Manager in Director Meeting *
                <select name="zonalManagerDirectorMeetingAttendance" value={form.zonalManagerDirectorMeetingAttendance} onChange={onInputChange}>
                  <option value="">Please select</option>
                  {attendanceOptions.map((o) => <option key={o} value={o}>{o}</option>)}
                </select>
              </label>
              <label>Zonal Manager in Strategy Meeting *
                <select name="zonalManagerStrategyMeetingAttendance" value={form.zonalManagerStrategyMeetingAttendance} onChange={onInputChange}>
                  <option value="">Please select</option>
                  {attendanceOptions.map((o) => <option key={o} value={o}>{o}</option>)}
                </select>
              </label>
              <label>Submitted By Email *<input name="submittedByEmail" value={form.submittedByEmail} onChange={onInputChange} /></label>
              <label>Participation in Pray With Me *<input name="participationPrayWithMe" value={form.participationPrayWithMe} onChange={onInputChange} /></label>
              <label>Total Registration for HSLHS *<input type="number" name="totalRegistrationHslhs" value={form.totalRegistrationHslhs} onChange={onInputChange} /></label>
              <label>Have you had your Herald Conference? *<input name="heraldConference" value={form.heraldConference} onChange={onInputChange} /></label>
              <label className="full">Any Testimony, Clarification or Concern?<textarea name="testimonyClarificationConcern" value={form.testimonyClarificationConcern} onChange={onInputChange} rows={4} /></label>
              <div className="button-row full">
                <button type="submit">{loading ? "Saving..." : "💾 Save to DB"}</button>
                <button type="button" onClick={() => downloadTemplate()}>📥 Download Template</button>
                <button type="button" onClick={() => downloadExport(form.submittedByEmail || undefined)}>📤 Export All Reports</button>
                <label className="upload-btn">📎 Upload CSV/Excel<input type="file" onChange={onUpload} accept=".csv,.xlsx,.xls" /></label>
              </div>
            </form>
          </>
        )}
        {message && <div className="message">{message}</div>}
      </div>

      <div className="glass card fade-in">
        <h2>📊 Reports</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Created</th><th>Zone</th><th>Manager</th><th>Remittance</th><th>Partners</th>
                <th>Testimonies</th><th>Translations</th><th>Outreaches</th><th>Media</th><th>Pastor</th>
                <th>Director</th><th>Strategy</th><th>Pray With Me</th><th>HSLHS Reg</th><th>Herald Conf</th><th>Notes</th><th>Email</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td><td>{r.createdAt}</td><td>{r.zoneName}</td><td>{r.zonalManager}</td><td>{r.totalPartnershipRemittance}</td><td>{r.newPartnersRecruited}</td>
                  <td>{r.testimoniesSubmitted}</td><td>{r.httnmTranslations}</td><td>{r.httnmOutreaches}</td><td>{r.outreachMediaSubmitted}</td><td>{r.zonalPastorAttendance}</td>
                  <td>{r.zonalManagerDirectorMeetingAttendance}</td><td>{r.zonalManagerStrategyMeetingAttendance}</td><td>{r.participationPrayWithMe}</td><td>{r.totalRegistrationHslhs}</td><td>{r.heraldConference}</td><td>{r.testimonyClarificationConcern}</td><td>{r.submittedByEmail}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
