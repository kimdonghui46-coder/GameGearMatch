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
  status: "PENDING" | "PAID" | "CANCELLED";
  createdAt: string;
  items: OrderItem[];
}
