import { useEffect, useRef, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";

function PaymentSuccessPage() {
  const [params] = useSearchParams();
  const requested = useRef(false);
  const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
  const [message, setMessage] = useState("결제를 승인하는 중입니다...");

  useEffect(() => {
    if (requested.current) return; requested.current = true;
    const paymentKey = params.get("paymentKey"); const orderId = params.get("orderId"); const amount = Number(params.get("amount"));
    if (!paymentKey || !orderId || !amount) { setStatus("error"); setMessage("결제 승인 정보가 올바르지 않습니다."); return; }
    api.post("/api/payments/confirm", { paymentKey, orderId, amount })
      .then(() => { setStatus("success"); setMessage("결제가 완료되었습니다."); window.dispatchEvent(new Event("cart-change")); })
      .catch(() => { setStatus("error"); setMessage("결제 승인에 실패했습니다. 주문 내역을 확인해 주세요."); });
  }, [params]);

  return <section className="auth-page"><div className="auth-card payment-result"><span className="eyebrow">PAYMENT RESULT</span><h1>{status === "success" ? "결제 완료" : status === "error" ? "결제 확인 필요" : "결제 승인 중"}</h1><p className={status === "error" ? "error" : "success"}>{message}</p>{status !== "loading" && <Link className="primary-button" to="/orders">주문 내역 보기</Link>}</div></section>;
}

export default PaymentSuccessPage;
