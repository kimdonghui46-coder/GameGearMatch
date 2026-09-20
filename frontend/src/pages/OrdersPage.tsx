import axios from "axios";
import { useEffect, useState } from "react";
import { Link, Navigate } from "react-router-dom";
import api from "../api/axios";
import type { Order } from "../types/order";

type ReviewDraft = { rating: number; content: string };

function OrdersPage() {
  const token = localStorage.getItem("accessToken");
  const [orders, setOrders] = useState<Order[]>([]);
  const [drafts, setDrafts] = useState<Record<number, ReviewDraft>>({});
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!token) { setLoading(false); return; }
    api.get<Order[]>("/api/orders").then(({ data }) => setOrders(data))
      .catch(() => setError("주문 내역을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [token]);

  if (!token) return <Navigate to="/login" replace />;

  const updateDraft = (productId: number, next: Partial<ReviewDraft>) => {
    setDrafts((current) => ({
      ...current,
      [productId]: {
        rating: current[productId]?.rating ?? 5,
        content: current[productId]?.content ?? "",
        ...next,
      },
    }));
  };

  const submitReview = async (productId: number) => {
    const draft = drafts[productId] ?? { rating: 5, content: "" };
    if (!draft.content.trim()) { setError("리뷰 내용을 입력해 주세요."); return; }
    setError(""); setMessage("");
    try {
      await api.post(`/api/products/${productId}/reviews`, draft);
      setMessage("리뷰가 등록되었습니다.");
      setDrafts((current) => ({ ...current, [productId]: { rating: 5, content: "" } }));
    } catch (requestError) {
      const serverMessage = axios.isAxiosError(requestError) ? requestError.response?.data?.message : null;
      setError(typeof serverMessage === "string" ? serverMessage : "리뷰를 등록하지 못했습니다. 이미 작성한 리뷰인지 확인해 주세요.");
    }
  };

  if (loading) return <p className="state-message">주문 내역을 불러오는 중입니다...</p>;

  return <section className="page-container orders-page">
    <div className="page-title"><span className="eyebrow">MY ORDERS</span><h1>주문 내역</h1><p>구매한 상품을 확인하고 리뷰를 작성할 수 있습니다.</p></div>
    {message && <p className="success">{message}</p>}{error && <p className="error">{error}</p>}
    {orders.length === 0 ? <div className="empty-box"><h2>주문 내역이 없습니다</h2><Link className="primary-button" to="/">상품 보러 가기</Link></div> :
      <div className="order-list">{orders.map((order) => <article className="order-card" key={order.id}>
        <header><div><b>주문 #{order.id}</b><span>{new Date(order.createdAt).toLocaleString("ko-KR")}</span></div><strong>{order.totalPrice.toLocaleString("ko-KR")}원</strong></header>
        {order.items.map((item) => { const draft = drafts[item.productId] ?? { rating: 5, content: "" }; return <div className="order-item" key={item.id}>
          <div className="order-product"><div className="order-thumb">{item.imageUrl ? <img src={item.imageUrl} alt={item.productName} /> : "GGM"}</div><div><small>{item.brand}</small><h3>{item.productName}</h3><span>{item.quantity}개 · {item.lineTotal.toLocaleString("ko-KR")}원</span></div></div>
          <div className="review-form"><select value={draft.rating} onChange={(e) => updateDraft(item.productId, { rating: Number(e.target.value) })}>{[5,4,3,2,1].map((rating) => <option key={rating} value={rating}>{"★".repeat(rating)} {rating}점</option>)}</select><input value={draft.content} onChange={(e) => updateDraft(item.productId, { content: e.target.value })} maxLength={1000} placeholder="구매한 상품의 리뷰를 작성해 주세요"/><button onClick={() => void submitReview(item.productId)}>리뷰 등록</button></div>
        </div>; })}
      </article>)}</div>}
  </section>;
}

export default OrdersPage;
