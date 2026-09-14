import axios from "axios";
import { type FormEvent, useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import api from "../api/axios";
import type { CurrentUser, UpdateProfileRequest } from "../types/auth";

function ProfilePage() {
  const token = localStorage.getItem("accessToken");
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [form, setForm] = useState<UpdateProfileRequest>({ name: "" });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!token) { setLoading(false); return; }
    api.get<CurrentUser>("/api/users/me")
      .then(({ data }) => { setUser(data); setForm({ name: data.name }); })
      .catch(() => setError("회원 정보를 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [token]);

  if (!token) return <Navigate to="/login" replace />;

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setSubmitting(true); setMessage(""); setError("");
    try {
      const { data } = await api.patch<CurrentUser>("/api/users/me", form);
      setUser(data); setForm({ name: data.name }); setMessage("프로필이 수정되었습니다.");
      window.dispatchEvent(new Event("auth-change"));
    } catch (requestError) {
      const serverMessage = axios.isAxiosError(requestError) ? requestError.response?.data?.message : null;
      setError(typeof serverMessage === "string" ? serverMessage : "프로필 수정에 실패했습니다.");
    } finally { setSubmitting(false); }
  };

  if (loading) return <p className="state-message">회원 정보를 불러오는 중...</p>;

  return <section className="auth-page profile-page"><form className="auth-card" onSubmit={submit}>
    <span className="eyebrow">MY PROFILE</span><h1>프로필 수정</h1>
    <p className="auth-description">화면에 표시되는 회원 이름을 변경할 수 있습니다.</p>
    <label>이름<input value={form.name} onChange={(event) => setForm({ name: event.target.value })} maxLength={50} required /></label>
    <label>이메일<input value={user?.email ?? ""} disabled /></label>
    <p className="profile-help">이메일은 로그인 계정이므로 변경할 수 없습니다.</p>
    {message && <p className="form-message success">{message}</p>}
    {error && <p className="form-message error">{error}</p>}
    <button className="submit-button" disabled={submitting} type="submit">{submitting ? "저장 중..." : "변경사항 저장"}</button>
  </form></section>;
}

export default ProfilePage;
