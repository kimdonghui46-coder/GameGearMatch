import { type FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Recommendation, RecommendationRequest } from "../types/recommendation";

function RecommendationPage() {
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");
  const [form, setForm] = useState<RecommendationRequest>({ category: "MOUSE", gameType: "FPS", maxPrice: 150000, preferredConnection: null, prioritizeLightweight: false });
  const [results, setResults] = useState<Recommendation[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState("");

  if (!token) return <Navigate to="/login" replace />;

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setLoading(true); setError(""); setSearched(true);
    try { const { data } = await api.post<Recommendation[]>("/api/recommendations", form); setResults(data); }
    catch { setError("추천 결과를 계산하지 못했습니다."); }
    finally { setLoading(false); }
  };

  const addToCart = async (productId: number) => {
    await api.post("/api/cart/items", { productId, quantity: 1 });
    window.dispatchEvent(new Event("cart-change")); navigate("/cart");
  };

  return <section className="page-container recommendation-page">
    <div className="page-title"><span className="eyebrow">SMART RECOMMENDATION</span><h1>게임별 장비 추천</h1><p>조건 필터링과 WSM 가중치 계산으로 나에게 맞는 상품을 찾습니다.</p></div>
    <form className="recommend-form" onSubmit={submit}>
      <label>장비 종류<select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value as RecommendationRequest["category"] })}><option value="MOUSE">마우스</option><option value="KEYBOARD">키보드</option><option value="HEADSET">헤드셋</option></select></label>
      <label>주로 하는 게임<select value={form.gameType} onChange={(e) => setForm({ ...form, gameType: e.target.value as RecommendationRequest["gameType"] })}><option value="FPS">FPS</option><option value="MOBA">MOBA/AOS</option><option value="MMORPG">MMORPG</option><option value="CASUAL">캐주얼</option></select></label>
      <label>최대 예산<input type="number" min="1" value={form.maxPrice ?? ""} onChange={(e) => setForm({ ...form, maxPrice: e.target.value ? Number(e.target.value) : null })}/></label>
      <label>연결 방식<select value={form.preferredConnection ?? ""} onChange={(e) => setForm({ ...form, preferredConnection: (e.target.value || null) as RecommendationRequest["preferredConnection"] })}><option value="">상관없음</option><option value="WIRED">유선</option><option value="WIRELESS">무선</option><option value="BOTH">유·무선</option></select></label>
      <label className="check-label"><input type="checkbox" checked={form.prioritizeLightweight} onChange={(e) => setForm({ ...form, prioritizeLightweight: e.target.checked })}/>가벼운 무게 우선</label>
      <button type="submit" disabled={loading}>{loading ? "계산 중..." : "추천 결과 보기"}</button>
    </form>
    <p className="form-note">※ 상품 가격은 기능 시연용 표시 가격이며 실제 판매가와 다를 수 있습니다.</p>
    {error && <p className="error">{error}</p>}{searched && !loading && results.length === 0 && <p className="state-message">조건에 맞는 상품이 없습니다.</p>}
    <div className="recommend-results">{results.map((item, index) => <article key={item.product.id}><div className="score-rank">#{index + 1}<b>{item.score}점</b></div><div className="recommend-image">{item.product.imageUrl ? <img src={item.product.imageUrl} alt={item.product.name}/> : "GGM"}</div><div className="recommend-info"><small>{item.product.brand}</small><h2>{item.product.name}</h2><p>{item.reasons.join(" · ")}</p><strong>{item.product.price.toLocaleString("ko-KR")}원</strong></div><button onClick={() => void addToCart(item.product.id)}>장바구니 담기</button></article>)}</div>
  </section>;
}

export default RecommendationPage;
