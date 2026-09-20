import { useEffect, useRef } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";

function PaymentFailPage() {
  const [params] = useSearchParams(); const cancelled = useRef(false);
  const message = params.get("message") ?? "결제가 취소되었거나 실패했습니다.";
  useEffect(() => { const orderId = params.get("merchantOrderId"); if (orderId && !cancelled.current) { cancelled.current = true; void api.post(`/api/orders/${orderId}/cancel`).catch(() => undefined); } }, [params]);
  return <section className="auth-page"><div className="auth-card payment-result"><span className="eyebrow">PAYMENT FAILED</span><h1>결제 실패</h1><p className="error">{message}</p><Link className="primary-button" to="/cart">장바구니로 돌아가기</Link></div></section>;
}

export default PaymentFailPage;
