import axios from "axios";
import { type FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { SignupRequest } from "../types/auth";

function SignupPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<SignupRequest>({ email: "", password: "", name: "" });
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setSubmitting(true); setMessage("");
    try {
      await api.post("/api/auth/signup", form);
      navigate("/login", { replace: true });
    } catch (error) {
      const serverMessage = axios.isAxiosError(error) ? error.response?.data?.message : null;
      setMessage(typeof serverMessage === "string" ? serverMessage : "회원가입에 실패했습니다. 입력 내용을 확인해 주세요.");
    } finally { setSubmitting(false); }
  };

  return (
    <section className="auth-page"><form className="auth-card" onSubmit={submit}>
      <span className="eyebrow">JOIN US</span><h1>회원가입</h1><p className="auth-description">간단한 정보 입력으로 맞춤 추천을 시작하세요.</p>
      <label>이름<input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="이름 입력" maxLength={50} required /></label>
      <label>이메일<input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="name@example.com" required /></label>
      <label>비밀번호<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="8자 이상 입력" minLength={8} required /></label>
      {message && <p className="form-message error">{message}</p>}
      <button className="submit-button" disabled={submitting} type="submit">{submitting ? "가입 중..." : "회원가입"}</button>
      <p className="auth-link">이미 회원인가요? <Link to="/login">로그인</Link></p>
    </form></section>
  );
}

export default SignupPage;
