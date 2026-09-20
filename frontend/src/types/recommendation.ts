import type { Product } from "./product";

export interface RecommendationRequest {
  category: "MOUSE" | "KEYBOARD" | "HEADSET";
  gameType: "FPS" | "MOBA" | "MMORPG" | "CASUAL";
  maxPrice: number | null;
  preferredConnection: "WIRED" | "WIRELESS" | "BOTH" | null;
  prioritizeLightweight: boolean;
}

export interface Recommendation {
  product: Product;
  score: number;
  reasons: string[];
}
