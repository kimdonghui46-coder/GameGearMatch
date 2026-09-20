interface TossWidgets {
  setAmount(amount: { currency: "KRW"; value: number }): Promise<void>;
  renderPaymentMethods(options: { selector: string; variantKey: string }): Promise<unknown>;
  renderAgreement(options: { selector: string; variantKey: string }): Promise<unknown>;
  requestPayment(options: { orderId: string; orderName: string; successUrl: string; failUrl: string; customerEmail?: string; customerName?: string }): Promise<void>;
}

interface Window {
  TossPayments: (clientKey: string) => { widgets(options: { customerKey: string }): TossWidgets };
}
