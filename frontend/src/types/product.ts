export interface ProductSpec {
  id: number;
  weight: number | null;
  dpi: number | null;
  pollingRate: number | null;
  buttonCount: number | null;
  switchType: string | null;
  keyboardLayout: string | null;
  noiseLevel: number | null;
  responseTime: number | null;
  batteryHours: number | null;
  microphone: boolean | null;
}

export interface ProductRequest {
  name: string;
  category: "MOUSE" | "KEYBOARD" | "HEADSET";
  brand: string;
  price: number;
  stock: number;
  connectionType: "WIRED" | "WIRELESS" | "BOTH" | null;
  imageUrl: string | null;
  description: string;
  spec: Omit<ProductSpec, "id">;
}

export interface Product {
  id: number;
  name: string;
  category: string;
  brand: string;
  price: number;
  stock: number;
  connectionType: string | null;
  imageUrl: string | null;
  description: string | null;
  spec: ProductSpec | null;
}
