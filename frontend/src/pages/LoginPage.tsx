import axios from "axios";
import { type FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { LoginRequest, LoginResponse } from "../types/auth";

function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<LoginRequest>({ email: "", password: "" });
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setSubmitting(true); setMessage("");
    try {
      const response = await api.post<LoginResponse>("/api/auth/login", form);
      localStorage.setItem("accessToken", response.data.accessToken);
      navigate("/"); window.location.reload();
    } catch (error) {
      setMessage(axios.isAxiosError(error) && error.response?.status === 401 ? "이메일 또는 비밀번호가 올바르지 않습니다." : "로그인에 실패했습니다.");
    } finally { setSubmitting(false); }
  };

  return (
    <section className="auth-page"><form className="auth-card" onSubmit={submit}>
      <span className="eyebrow">WELCOME BACK</span><h1>로그인</h1><p className="auth-description">GameGearMatch에서 나에게 맞는 장비를 찾아보세요.</p>
      <label>이메일<input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="name@example.com" required /></label>
      <label>비밀번호<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="비밀번호 입력" required /></label>
      {message && <p className="form-message error">{message}</p>}
      <button className="submit-button" disabled={submitting} type="submit">{submitting ? "로그인 중..." : "로그인"}</button>
      <p className="auth-link">아직 회원이 아닌가요? <Link to="/signup">회원가입</Link></p>
    </form></section>
  );
}

export default LoginPage;
