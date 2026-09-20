import { useEffect, useRef, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import type { Order } from "../types/order";

function PaymentPage() {
  const location = useLocation();
  const order = (location.state as { order?: Order } | null)?.order;
  const initialized = useRef(false);
  const [ready, setReady] = useState(false);
  const [error, setError] = useState("");
  const widgetsRef = useRef<TossWidgets | null>(null);

  useEffect(() => {
    if (!order || initialized.current) return;
    initialized.current = true;
    void (async () => {
      try {
        const clientKey = import.meta.env.VITE_TOSS_CLIENT_KEY;
        if (!clientKey || !window.TossPayments) throw new Error("토스 클라이언트 키가 설정되지 않았습니다.");
        let customerKey = localStorage.getItem("tossCustomerKey");
        if (!customerKey) {
          const randomId = globalThis.crypto?.randomUUID?.()
            ?? `${Date.now()}-${Math.random().toString(36).slice(2)}`;
          customerKey = `${randomId}-ggm`;
          localStorage.setItem("tossCustomerKey", customerKey);
        }
        const widgets = window.TossPayments(clientKey).widgets({ customerKey });
        widgetsRef.current = widgets;
        await widgets.setAmount({ currency: "KRW", value: order.totalPrice });
        await Promise.all([
          widgets.renderPaymentMethods({ selector: "#payment-method", variantKey: "DEFAULT" }),
          widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
        ]);
        setReady(true);
      } catch (cause) {
        setError(cause instanceof Error ? cause.message : "결제창을 불러오지 못했습니다.");
      }
    })();
  }, [order]);

  if (!localStorage.getItem("accessToken")) return <Navigate to="/login" replace />;
  if (!order) return <Navigate to="/cart" replace />;

  const pay = async () => {
    try {
      const origin = window.location.origin;
      await widgetsRef.current?.requestPayment({
        orderId: order.paymentOrderId, orderName: order.orderName,
        successUrl: `${origin}/payment/success`,
        failUrl: `${origin}/payment/fail?merchantOrderId=${encodeURIComponent(order.paymentOrderId)}`,
      });
    } catch { setError("결제 요청을 시작하지 못했습니다."); }
  };

  return <section className="page-container payment-page"><div className="page-title"><span className="eyebrow">TOSS PAYMENTS</span><h1>결제하기</h1><p>{order.orderName}</p></div><div className="payment-box"><div id="payment-method"/><div id="agreement"/>{error && <p className="error">{error}</p>}<button disabled={!ready} onClick={() => void pay()}>{ready ? `${order.totalPrice.toLocaleString("ko-KR")}원 결제하기` : "결제창 준비 중..."}</button></div></section>;
}

export default PaymentPage;
