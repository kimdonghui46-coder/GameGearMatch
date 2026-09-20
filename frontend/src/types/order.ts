export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  brand: string;
  imageUrl: string | null;
  quantity: number;
  orderPrice: number;
  lineTotal: number;
}

export interface Order {
  id: number;
  paymentOrderId: string;
  orderName: string;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
  recipientName: string;
  recipientPhone: string;
  postalCode: string;
  address: string;
  deliveryRequest: string | null;
  items: OrderItem[];
}

export type OrderStatus = "PENDING" | "PAID" | "PREPARING" | "SHIPPING" | "DELIVERED" | "CANCELLED_REFUNDED";

export const orderStatusLabel: Record<OrderStatus, string> = {
  PENDING: "결제 대기", PAID: "결제 완료", PREPARING: "배송 준비",
  SHIPPING: "배송 중", DELIVERED: "배송 완료", CANCELLED_REFUNDED: "취소/환불",
};
