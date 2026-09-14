export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  brand: string;
  price: number;
  quantity: number;
  lineTotal: number;
  imageUrl: string | null;
  stock: number;
}

export interface Cart {
  cartId: number;
  items: CartItem[];
  totalQuantity: number;
  totalPrice: number;
}
