const API_BASE = "http://localhost:8080/api/reports";

export async function fetchReports(email) {
  const query = email ? `?email=${encodeURIComponent(email)}` : "";
  const res = await fetch(`${API_BASE}${query}`);
  if (!res.ok) throw new Error("Unable to fetch reports");
  return res.json();
}

export async function submitReport(payload) {
  const res = await fetch(API_BASE, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Unable to submit report");
  return res.json();
}

export async function downloadExcel(email) {
  const query = email ? `?email=${encodeURIComponent(email)}` : "";
  const res = await fetch(`${API_BASE}/export/excel${query}`);
  if (!res.ok) throw new Error("Unable to download excel");
  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "weekly-reports.xlsx";
  a.click();
  window.URL.revokeObjectURL(url);
}
