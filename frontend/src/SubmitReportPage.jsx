import { useEffect, useMemo, useState } from "react";
import { downloadExcel, fetchReports, submitReport } from "./reportsApi";

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

export default function SubmitReportPage() {
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
    if (hasMissing) {
      setMessage("Please fill all required fields.");
      return;
    }

    setLoading(true);
    try {
      const payload = {
        ...form,
        totalPartnershipRemittance: Number(form.totalPartnershipRemittance),
        newPartnersRecruited: Number(form.newPartnersRecruited),
        testimoniesSubmitted: Number(form.testimoniesSubmitted),
        httnmTranslations: Number(form.httnmTranslations),
        httnmOutreaches: Number(form.httnmOutreaches),
        outreachMediaSubmitted: Number(form.outreachMediaSubmitted),
        totalRegistrationHslhs: Number(form.totalRegistrationHslhs)
      };
      await submitReport(payload);
      setForm((prev) => ({ ...initialForm, submittedByEmail: prev.submittedByEmail }));
      await loadReports(payload.submittedByEmail);
      setMessage("Report submitted successfully.");
    } catch (err) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function onDownload() {
    try {
      await downloadExcel(form.submittedByEmail || undefined);
    } catch (err) {
      setMessage(err.message);
    }
  }

  return (
    <div className="page">
      <h1>Kingsforms Report - October Week 1 (7th October)</h1>
      <p>Submit report and save directly in database.</p>

      <form className="card form-grid" onSubmit={onSubmit}>
        <label>Name of Zone *
          <input name="zoneName" value={form.zoneName} onChange={onInputChange} />
        </label>
        <label>Zonal Manager *
          <input name="zonalManager" value={form.zonalManager} onChange={onInputChange} />
        </label>
        <label>Total Partnership Remittance (Target 10,000) *
          <input type="number" name="totalPartnershipRemittance" value={form.totalPartnershipRemittance} onChange={onInputChange} />
        </label>
        <label>New Partners Recruited (Target 10) *
          <input type="number" name="newPartnersRecruited" value={form.newPartnersRecruited} onChange={onInputChange} />
        </label>
        <label>Testimonies Submitted (Target 50) *
          <input type="number" name="testimoniesSubmitted" value={form.testimoniesSubmitted} onChange={onInputChange} />
        </label>
        <label>HTTNM Translations (Target 2) *
          <input type="number" name="httnmTranslations" value={form.httnmTranslations} onChange={onInputChange} />
        </label>
        <label>HTTNM Outreaches (Target 10) *
          <input type="number" name="httnmOutreaches" value={form.httnmOutreaches} onChange={onInputChange} />
        </label>
        <label>Pictures/Videos Submitted (Target 10) *
          <input type="number" name="outreachMediaSubmitted" value={form.outreachMediaSubmitted} onChange={onInputChange} />
        </label>
        <label>Zonal Pastor attendance in weekly meeting? *
          <select name="zonalPastorAttendance" value={form.zonalPastorAttendance} onChange={onInputChange}>
            <option value="">Please select</option>
            {attendanceOptions.map((option) => <option key={option} value={option}>{option}</option>)}
          </select>
        </label>
        <label>Zonal Manager attendance in Director's weekly meeting? *
          <select name="zonalManagerDirectorMeetingAttendance" value={form.zonalManagerDirectorMeetingAttendance} onChange={onInputChange}>
            <option value="">Please select</option>
            {attendanceOptions.map((option) => <option key={option} value={option}>{option}</option>)}
          </select>
        </label>
        <label>Zonal Manager attendance in Managers strategy meeting? *
          <select name="zonalManagerStrategyMeetingAttendance" value={form.zonalManagerStrategyMeetingAttendance} onChange={onInputChange}>
            <option value="">Please select</option>
            {attendanceOptions.map((option) => <option key={option} value={option}>{option}</option>)}
          </select>
        </label>
        <label>Submitted By Email *
          <input name="submittedByEmail" value={form.submittedByEmail} onChange={onInputChange} />
        </label>
        <label>Participation in Pray With Me *
          <input name="participationPrayWithMe" value={form.participationPrayWithMe} onChange={onInputChange} />
        </label>
        <label>Total Registration for HSLHS *
          <input type="number" name="totalRegistrationHslhs" value={form.totalRegistrationHslhs} onChange={onInputChange} />
        </label>
        <label>Have you had your Herald Conference? *
          <input name="heraldConference" value={form.heraldConference} onChange={onInputChange} />
        </label>
        <label className="full">Any Testimony, Clarification or Concern?
          <textarea name="testimonyClarificationConcern" value={form.testimonyClarificationConcern} onChange={onInputChange} rows={4} />
        </label>

        <div className="button-row full">
          <button type="submit" disabled={loading}>{loading ? "Submitting..." : "Submit Report"}</button>
          <button type="button" onClick={onDownload}>Download Excel</button>
          <button type="button" onClick={() => loadReports()}>Refresh Reports</button>
        </div>
      </form>

      {message && <p className="message">{message}</p>}

      <div className="card">
        <h2>Submitted Reports</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Created</th>
                <th>Zone</th>
                <th>Zonal Manager</th>
                <th>Remittance</th>
                <th>Partners</th>
                <th>Testimonies</th>
                <th>Translations</th>
                <th>Outreaches</th>
                <th>Media</th>
                <th>Pastor Attendance</th>
                <th>Director Meeting</th>
                <th>Strategy Meeting</th>
                <th>Pray With Me</th>
                <th>HSLHS Reg</th>
                <th>Herald Conf</th>
                <th>Concern</th>
                <th>Submitted By</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.createdAt}</td>
                  <td>{r.zoneName}</td>
                  <td>{r.zonalManager}</td>
                  <td>{r.totalPartnershipRemittance}</td>
                  <td>{r.newPartnersRecruited}</td>
                  <td>{r.testimoniesSubmitted}</td>
                  <td>{r.httnmTranslations}</td>
                  <td>{r.httnmOutreaches}</td>
                  <td>{r.outreachMediaSubmitted}</td>
                  <td>{r.zonalPastorAttendance}</td>
                  <td>{r.zonalManagerDirectorMeetingAttendance}</td>
                  <td>{r.zonalManagerStrategyMeetingAttendance}</td>
                  <td>{r.participationPrayWithMe}</td>
                  <td>{r.totalRegistrationHslhs}</td>
                  <td>{r.heraldConference}</td>
                  <td>{r.testimonyClarificationConcern}</td>
                  <td>{r.submittedByEmail}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
